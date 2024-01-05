package toy.yogiyo.core.review.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.order.domain.Order;
import toy.yogiyo.core.review.domain.Review;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewCreateRequest {

    @NotNull(message = "주문 ID가 반드시 들어가야 합니다")
    private Long orderId;
    private BigDecimal tasteScore;
    private BigDecimal quantityScore;
    private BigDecimal deliveryScore;
    @NotEmpty
    private String content;

    @NotNull(message = "음식점 ID가 반드시 들어가야 합니다")
    private Long shopId;
    private String shopName;

    public Review toReview(Member member, Order order){
        return Review.builder()
                .tasteScore(tasteScore)
                .quantityScore(quantityScore)
                .deliveryScore(deliveryScore)
                .totalScore(tasteScore.add(quantityScore).add(deliveryScore).divide(BigDecimal.valueOf(3)))
                .content(content)
                .shopId(shopId)
                .shopName(shopName)
                .member(member)
                .order(order)
                .build();
    }
}
