package toy.yogiyo.core.deliveryplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPriceInfo;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPriceUpdateRequest {

    @NotNull
    private List<DeliveryPriceDto> deliveryPrices;

    public List<DeliveryPriceInfo> toDeliveryPriceInfos() {
        return this.deliveryPrices.stream()
                .map(DeliveryPriceDto::toDeliveryPriceInfo)
                .collect(Collectors.toList());
    }

}
