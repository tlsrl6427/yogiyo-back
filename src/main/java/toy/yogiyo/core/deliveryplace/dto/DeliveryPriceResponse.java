package toy.yogiyo.core.deliveryplace.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPlace;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPriceInfo;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class DeliveryPriceResponse {

    private List<DeliveryPriceDto> deliveryPrices;

    public static DeliveryPriceResponse from(DeliveryPlace deliveryPlace) {
        return DeliveryPriceResponse.builder()
                .deliveryPrices(deliveryPlace.getDeliveryPriceInfos().stream()
                        .map(d -> new DeliveryPriceDto(d.getOrderPrice(), d.getDeliveryPrice()))
                        .collect(Collectors.toList()))
                .build();
    }
}
