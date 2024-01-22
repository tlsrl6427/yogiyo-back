package toy.yogiyo.core.shop.dto.scroll;

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
    private Long orderNum;
    private Long reviewNum;
    private BigDecimal totalScore;
    private Double distance;
    private Integer deliveryTime;
    private Integer minDeliveryPrice;
    private Integer maxDeliveryPrice;
    private String icon;
}
