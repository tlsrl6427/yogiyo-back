package toy.yogiyo.core.deliveryplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPlace;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPlaceListResponse {

    private List<DeliveryPlaceDto> deliveryPlaces;

    public static DeliveryPlaceListResponse from(List<DeliveryPlace> deliveryPlaces) {
        return DeliveryPlaceListResponse.builder()
                .deliveryPlaces(deliveryPlaces.stream()
                        .map(DeliveryPlaceDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    public static class DeliveryPlaceDto {

        private Long id;
        private String name;
        private int orderPrice;
        private int deliveryPrice;

        public DeliveryPlaceDto(DeliveryPlace deliveryPlace) {
            this.id = deliveryPlace.getId();
            this.name = deliveryPlace.getName();
            this.orderPrice = deliveryPlace.getMinOrderPrice();
            this.deliveryPrice = deliveryPlace.getMinDeliveryPrice();
        }
    }
}
