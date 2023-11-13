package toy.yogiyo.core.shop.dto;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ShopScrollResponse {

    private Long shopId;
    private String shopName;
    private Double totalScore;
    private Double distance;
    private int deliveryTime;
    private int minDeliveryPrice;
    private int maxDeliveryPrice;
    private String icon;
}
