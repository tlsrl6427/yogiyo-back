package toy.yogiyo.core.deliveryplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPlace;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPlaceGetResponse {

    private Long id;
    private String code;
    private String name;
    private int deliveryTime;

    public static DeliveryPlaceGetResponse from(DeliveryPlace deliveryPlace) {
        return DeliveryPlaceGetResponse.builder()
                .id(deliveryPlace.getId())
                .code(deliveryPlace.getCode())
                .name(deliveryPlace.getName())
                .deliveryTime(deliveryPlace.getDeliveryTime())
                .build();
    }
}
