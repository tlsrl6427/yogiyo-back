package toy.yogiyo.core.menuoption.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.owner.domain.Owner;

import javax.persistence.EntityManager;

import static toy.yogiyo.core.menuoption.domain.QMenuOptionGroup.*;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Component("menuOptionGroupPermissionEvaluator")
public class MenuOptionGroupPermissionEvaluator {

    private final JPAQueryFactory queryFactory;

    public MenuOptionGroupPermissionEvaluator(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public boolean hasWritePermission(Authentication authentication, Long menuOptionGroupId) {
        Owner owner = (Owner) authentication.getPrincipal();

        Integer fetchOne = queryFactory
                .selectOne()
                .from(menuOptionGroup)
                .where(menuOptionGroup.id.eq(menuOptionGroupId),
                        shop.owner.id.eq(owner.getId()))
                .join(menuOptionGroup.shop, shop)
                .fetchFirst();

        return fetchOne != null;
    }

}
