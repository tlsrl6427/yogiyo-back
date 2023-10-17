package toy.yogiyo.core.shop.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.owner.domain.Owner;

import javax.persistence.EntityManager;

import static toy.yogiyo.core.shop.domain.QShop.shop;

@Component("shopPermissionEvaluator")
public class ShopPermissionEvaluator {

    private final JPAQueryFactory queryFactory;

    public ShopPermissionEvaluator(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public boolean hasWritePermission(Authentication authentication, Long shopId) {
        Owner owner = (Owner) authentication.getPrincipal();

        Integer fetchOne = queryFactory
                .selectOne()
                .from(shop)
                .where(shop.id.eq(shopId),
                        shop.owner.id.eq(owner.getId()))
                .fetchFirst();

        return fetchOne != null;
    }
}
