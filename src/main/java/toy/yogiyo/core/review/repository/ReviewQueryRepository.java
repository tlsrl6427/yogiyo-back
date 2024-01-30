package toy.yogiyo.core.review.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.common.util.OrderByNull;
import toy.yogiyo.core.review.dto.ReviewManagementResponse;
import toy.yogiyo.core.review.dto.ReviewQueryCondition;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static toy.yogiyo.core.member.domain.QMember.*;
import static toy.yogiyo.core.order.domain.QOrderItem.orderItem;
import static toy.yogiyo.core.review.domain.QReview.*;
import static toy.yogiyo.core.review.domain.QReviewImage.reviewImage;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Scroll<ReviewManagementResponse> shopReviewScroll(Long shopId, ReviewQueryCondition condition) {
        // 리뷰 조회
        List<ReviewManagementResponse> response = queryFactory.select(Projections.fields(ReviewManagementResponse.class,
                        review.id,
                        review.totalScore,
                        review.tasteScore,
                        review.quantityScore,
                        review.deliveryScore,
                        review.content,
                        member.nickname.as("memberName"),
                        review.createdAt
                ))
                .from(review)
                .join(review.member, member)
                .where(review.shopId.eq(shopId),
                        dateBetween(condition.getStartDate(), condition.getEndDate()),
                        status(condition.getStatus()))
                .orderBy(sortBy(condition.getSort()))
                .offset(condition.getOffset())
                .limit(condition.getLimit() + 1)
                .fetch();

        boolean hasNext = false;
        if (response.size() > condition.getLimit()) {
            hasNext = true;
            response.remove(response.size()-1);
        }

        // 리뷰 이미지 조회
        List<Long> reviewIds = response.stream().map(ReviewManagementResponse::getId)
                .collect(Collectors.toList());

        List<Tuple> reviewImages = queryFactory.select(reviewImage.review.id, reviewImage.imgSrc)
                .from(reviewImage)
                .where(reviewImage.review.id.in(reviewIds))
                .fetch();

        for (Tuple image : reviewImages) {
            response.stream()
                    .filter(r -> r.getId().equals(image.get(reviewImage.review.id)))
                    .findFirst()
                    .ifPresent(r -> r.addReviewImage(image.get(reviewImage.imgSrc)));
        }

        // 메뉴 조회
        List<Tuple> menus = queryFactory.select(review.id, orderItem.menuName, orderItem.quantity)
                .from(orderItem)
                .join(review).on(review.id.in(reviewIds))
                .where(orderItem.order.id.eq(review.order.id))
                .fetch();

        for (Tuple menu : menus) {
            response.stream()
                    .filter(r -> r.getId().equals(menu.get(review.id)))
                    .findFirst()
                    .ifPresent(r -> r.addMenu(new ReviewManagementResponse.MenuDto(menu.get(orderItem.menuName), menu.get(orderItem.quantity))));

        }

        return new Scroll<>(response, condition.getOffset() + response.size(), hasNext);
    }

    private BooleanExpression dateBetween(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            return null;
        }
        return review.createdAt.between(startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    private BooleanExpression status(ReviewQueryCondition.Status status) {
        if(status == null) return null;

        switch (status) {
            case ALL:
                return null;
            case NO_REPLY:
                return review.ownerReply.isNotNull();
        }
        return null;
    }

    private OrderSpecifier<?> sortBy(ReviewQueryCondition.Sort sort) {
        if(sort == null) return OrderByNull.DEFAULT;

        switch (sort) {
            case LATEST:
                return new OrderSpecifier<>(Order.DESC, review.createdAt);
            case RATING_HIGH:
                return new OrderSpecifier<>(Order.DESC, review.totalScore);
            case RATING_LOW:
                return new OrderSpecifier<>(Order.ASC, review.totalScore);
        }
        return OrderByNull.DEFAULT;
    }
}
