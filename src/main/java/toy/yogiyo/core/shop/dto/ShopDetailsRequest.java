package toy.yogiyo.core.shop.dto;


import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class ShopDetailsRequest {
    @NotNull
    private Long shopId;
    @NotNull
    private String code;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
