package toy.yogiyo.core.shop.dto;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ShopScrollResponse {

    private Long shopId;
    private String shopName;
    private BigDecimal totalScore;
    private Double distance;
    private Integer minDeliveryTime;
    private Integer maxDeliveryTime;
    private Integer minDeliveryPrice;
    private Integer maxDeliveryPrice;
    private String icon;
}
