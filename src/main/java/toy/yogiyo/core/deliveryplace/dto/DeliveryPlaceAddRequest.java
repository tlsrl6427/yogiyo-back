package toy.yogiyo.core.deliveryplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPlace;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPlaceAddRequest {

    @NotBlank(message = "법정동 코드를 입력해 주세요.")
    private String code;
    @NotBlank(message = "법정동명을 입력해 주세요.")
    private String name;

    @Min(value = 0, message = "0 이상 입력해 주세요.")
    private int deliveryTime;

    public DeliveryPlace toDeliveryPlace() {
        return DeliveryPlace.builder()
                .code(code)
                .name(name)
                .deliveryTime(deliveryTime)
                .build();
    }
}
