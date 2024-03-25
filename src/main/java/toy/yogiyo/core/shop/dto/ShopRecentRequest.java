package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class ShopRecentRequest {

    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
    @NotNull
    private String code;
}
