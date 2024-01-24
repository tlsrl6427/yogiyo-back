package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailsResponse {
    private Long id;
    private String name;
    private Long reviewNum;
    private Long likeNum;
    private BigDecimal totalScore;
    private String banner;
    private Double distance;
    private Integer minOrderPrice;
    private Integer minDeliveryPrice;
    private Boolean isLike;
}
