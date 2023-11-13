package toy.yogiyo.core.review.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.order.domain.Order;
import toy.yogiyo.core.review.domain.Review;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewCreateRequest {

    @NotBlank(message = "주문 ID가 반드시 들어가야 합니다")
    private Long orderId;
    private float tasteScore;
    private float quantityScore;
    private float deliveryScore;
    @NotEmpty
    private String content;

    @NotBlank(message = "음식점 ID가 반드시 들어가야 합니다")
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
