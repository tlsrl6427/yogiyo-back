package toy.yogiyo.core.Review.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.owner.domain.Owner;

import static toy.yogiyo.core.Review.domain.QReview.*;
import static toy.yogiyo.core.shop.domain.QShop.*;

@Component("reviewManagementPermissionEvaluator")
@RequiredArgsConstructor
public class ReviewManagementPermissionEvaluator {

    private final JPAQueryFactory jpaQueryFactory;

    public boolean hasPermission(Authentication authentication, Long reviewId) {
        Owner owner = (Owner) authentication.getPrincipal();
        Integer fetchOne = jpaQueryFactory.selectOne()
                .from(review)
                .join(shop).on(review.shopId.eq(shop.id))
                .where(review.id.eq(reviewId),
                        shop.owner.id.eq(owner.getId()))
                .fetchFirst();

        return fetchOne != null;
    }
}
