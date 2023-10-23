package toy.yogiyo.core.Review.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.core.Review.domain.Review;
import toy.yogiyo.core.Review.dto.ReviewQueryCondition;

import java.time.LocalDate;
import java.util.List;

import static toy.yogiyo.core.Member.domain.QMember.*;
import static toy.yogiyo.core.Review.domain.QReview.*;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Scroll<Review> shopReviewScroll(Long shopId, ReviewQueryCondition condition) {
        long offset = (long) condition.getNumber() * condition.getSize();

        List<Review> reviews = queryFactory.selectFrom(review)
                .leftJoin(review.member, member).fetchJoin()
                .where(review.shopId.eq(shopId),
                        dateBetween(condition.getStartDate(), condition.getEndDate()),
                        status(condition.getStatus()))
                .orderBy(sortBy(condition.getSort()))
                .offset(offset)
                .limit(condition.getSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (reviews.size() > condition.getSize()) {
            hasNext = true;
            reviews.remove(reviews.size()-1);
        }

        return new Scroll<>(reviews, condition.getNumber(), condition.getSize(), hasNext);
    }

    private BooleanExpression dateBetween(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            return null;
        }
        return review.createdAt.between(startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    private BooleanExpression status(ReviewQueryCondition.Status status) {
        switch (status) {
            case ALL:
                return null;
            case NO_REPLY:
                return review.ownerReply.isNotNull();
        }
        return null;
    }

    private OrderSpecifier<?> sortBy(ReviewQueryCondition.Sort sort) {
        switch (sort) {
            case LATEST:
                return new OrderSpecifier<>(Order.DESC, review.createdAt);
            case RATING_HIGH:
                return new OrderSpecifier<>(Order.DESC, review.tasteScore);
            case RATING_LOW:
                return new OrderSpecifier<>(Order.ASC, review.tasteScore);
        }
        return null;
    }
}
