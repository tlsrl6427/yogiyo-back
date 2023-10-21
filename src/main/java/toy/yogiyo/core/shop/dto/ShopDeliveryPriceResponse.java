package toy.yogiyo.core.shop.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ShopDeliveryPriceResponse {

    private List<DeliveryPriceDto> deliveryPrices;

    public static ShopDeliveryPriceResponse from(Shop shop) {
        return ShopDeliveryPriceResponse.builder()
                .deliveryPrices(shop.getDeliveryPriceInfos().stream()
                        .map(d -> new DeliveryPriceDto(d.getOrderPrice(), d.getDeliveryPrice()))
                        .collect(Collectors.toList()))
                .build();
    }
}
