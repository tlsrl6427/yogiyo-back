package toy.yogiyo.core.shop.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@Builder
public class ShopBusinessHourResponse {

    private String businessHours;

    public static ShopBusinessHourResponse from(Shop shop) {
        return ShopBusinessHourResponse.builder()
                .businessHours(shop.getBusinessHours())
                .build();
    }
}
