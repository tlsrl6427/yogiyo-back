package toy.yogiyo.core.menuoption.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.owner.domain.Owner;

import javax.persistence.EntityManager;

import static toy.yogiyo.core.menuoption.domain.QMenuOption.*;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Component("menuOptionPermissionEvaluator")
public class MenuOptionPermissionEvaluator {

    private final JPAQueryFactory queryFactory;

    public MenuOptionPermissionEvaluator(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public boolean hasWritePermission(Authentication authentication, Long menuOptionId) {
        Owner owner = (Owner) authentication.getPrincipal();

        Integer fetchOne = queryFactory
                .selectOne()
                .from(menuOption)
                .where(menuOption.id.eq(menuOptionId),
                        shop.owner.id.eq(owner.getId()))
                .join(menuOption.menuOptionGroup.shop, shop)
                .fetchFirst();

        return fetchOne != null;
    }
}
