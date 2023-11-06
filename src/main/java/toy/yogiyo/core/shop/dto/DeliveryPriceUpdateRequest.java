package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPriceUpdateRequest {

    private List<DeliveryPriceDto> deliveryPrices;

    public List<DeliveryPriceInfo> toDeliveryPriceInfos() {
        return this.deliveryPrices.stream()
                .map(DeliveryPriceDto::toDeliveryPriceInfo)
                .collect(Collectors.toList());
    }

}
