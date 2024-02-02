package toy.yogiyo.core.menu.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import toy.yogiyo.core.menu.dto.MenuSearchRequest;
import toy.yogiyo.core.menu.dto.MenuSearchResponse;
import toy.yogiyo.core.menu.dto.member.MenuDetailsGetResponse;

import javax.persistence.EntityManager;

import java.util.List;

import static toy.yogiyo.core.menu.domain.QMenu.menu;
import static toy.yogiyo.core.menu.domain.QMenuGroup.menuGroup;
import static toy.yogiyo.core.menuoption.domain.QMenuOption.menuOption;
import static toy.yogiyo.core.menuoption.domain.QMenuOptionGroup.menuOptionGroup;
import static toy.yogiyo.core.menuoption.domain.QOptionGroupLinkMenu.optionGroupLinkMenu;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Repository
public class MenuQueryRepository {

    private final JPAQueryFactory queryFactory;

    public MenuQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public MenuDetailsGetResponse getDetails(Long menuId) {
        MenuDetailsGetResponse response = queryFactory.select(Projections.fields(MenuDetailsGetResponse.class,
                        menu.id,
                        menu.content,
                        menu.name,
                        menu.picture,
                        menu.price,
                        menu.reviewNum
                ))
                .from(menu)
                .where(menu.id.eq(menuId))
                .fetchOne();

        List<MenuDetailsGetResponse.OptionGroupDto> optionGroups = queryFactory
                .from(optionGroupLinkMenu)
                .join(optionGroupLinkMenu.menuOptionGroup, menuOptionGroup)
                .join(menuOptionGroup.menuOptions, menuOption)
                .where(optionGroupLinkMenu.menu.id.eq(menuId))
                .orderBy(menuOptionGroup.position.asc(), menuOption.position.asc())
                .transform(GroupBy.groupBy(menuOptionGroup.id).list(
                        Projections.fields(MenuDetailsGetResponse.OptionGroupDto.class,
                                menuOptionGroup.id,
                                menuOptionGroup.name,
                                menuOptionGroup.count,
                                menuOptionGroup.isPossibleCount,
                                menuOptionGroup.optionType,
                                GroupBy.list(Projections.fields(MenuDetailsGetResponse.OptionDto.class,
                                        menuOption.id,
                                        menuOption.content,
                                        menuOption.price
                                )).as("options")
                        )
                ));

        response.setOptionGroups(optionGroups);

        return response;
    }

    public MenuSearchResponse searchMenu(MenuSearchRequest request) {
        List<MenuSearchResponse.MenuDto> result = queryFactory.select(Projections.fields(MenuSearchResponse.MenuDto.class,
                        menu.id,
                        menu.name,
                        menu.content,
                        menu.picture,
                        menu.price,
                        menu.visible
                ))
                .from(menu)
                .join(menu.menuGroup, menuGroup)
                .join(menuGroup.shop, shop)
                .where(shop.id.eq(request.getShopId()).and(menu.name.contains(request.getKeyword())))
                .fetch();

        return MenuSearchResponse.builder()
                .menus(result)
                .build();
    }
}
