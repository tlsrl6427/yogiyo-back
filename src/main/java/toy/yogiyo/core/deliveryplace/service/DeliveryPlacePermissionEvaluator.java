package toy.yogiyo.core.deliveryplace.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.owner.domain.Owner;

import javax.persistence.EntityManager;

import static toy.yogiyo.core.deliveryplace.domain.QDeliveryPlace.deliveryPlace;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Component
public class DeliveryPlacePermissionEvaluator {

    private final JPAQueryFactory queryFactory;

    public DeliveryPlacePermissionEvaluator(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public boolean hasWritePermission(Authentication authentication, Long deliveryPlaceId) {
        Owner owner = (Owner) authentication.getPrincipal();

        Integer fetchOne = queryFactory
                .selectOne()
                .from(deliveryPlace)
                .where(deliveryPlace.id.eq(deliveryPlaceId),
                        shop.owner.id.eq(owner.getId()))
                .join(deliveryPlace.shop, shop)
                .fetchFirst();

        return fetchOne != null;
    }
}
