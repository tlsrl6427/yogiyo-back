package toy.yogiyo.core.menu.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.owner.domain.Owner;

import javax.persistence.EntityManager;

import static toy.yogiyo.core.menu.domain.QMenu.*;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Component("menuPermissionEvaluator")
public class MenuPermissionEvaluator {

    private final JPAQueryFactory queryFactory;

    public MenuPermissionEvaluator(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public boolean hasWritePermission(Authentication authentication, Long menuId) {
        Owner owner = (Owner) authentication.getPrincipal();

        Integer fetchOne = queryFactory
                .selectOne()
                .from(menu)
                .where(menu.id.eq(menuId),
                        shop.owner.id.eq(owner.getId()))
                .join(menu.menuGroup.shop, shop)
                .fetchFirst();

        return fetchOne != null;
    }
}
