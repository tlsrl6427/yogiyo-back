package toy.yogiyo.core.menu.service;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.owner.domain.Owner;

import javax.persistence.EntityManager;

import static toy.yogiyo.core.menu.domain.QMenuGroup.*;
import static toy.yogiyo.core.shop.domain.QShop.*;

@Component("menuGroupPermissionEvaluator")
public class MenuGroupPermissionEvaluator {

    private final JPAQueryFactory queryFactory;

    public MenuGroupPermissionEvaluator(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public boolean hasWritePermission(Authentication authentication, Long menuGroupId) {
        Owner owner = (Owner) authentication.getPrincipal();

        Integer fetchOne = queryFactory
                .selectOne()
                .from(menuGroup)
                .where(menuGroup.id.eq(menuGroupId),
                        shop.owner.id.eq(owner.getId()))
                .join(menuGroup.shop, shop)
                .fetchFirst();

        return fetchOne != null;
    }
}
