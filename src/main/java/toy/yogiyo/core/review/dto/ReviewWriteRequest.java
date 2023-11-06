package toy.yogiyo.core.review.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.order.domain.Order;
import toy.yogiyo.core.review.domain.Review;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewWriteRequest {

    private Long orderId;
    private float tasteScore;
    private float quantityScore;
    private float deliveryScore;
    private String content;
    private Long shopId;
    private String shopName;

    public Review toReview(Member member, Order order){
        return Review.builder()
                .tasteScore(tasteScore)
                .quantityScore(quantityScore)
                .deliveryScore(deliveryScore)
                .totalScore((tasteScore+quantityScore+deliveryScore)/3)
                .content(content)
                .shopId(shopId)
                .shopName(shopName)
                .member(member)
                .order(order)
                .build();
    }
}
