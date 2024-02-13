package toy.yogiyo.core.deliveryplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPlaceAdjustmentRequest {
    @NotNull
    private AdjustmentType adjustmentType;
    @Min(100)
    private int value;
}
