package toy.yogiyo.core.shop.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NewShopListRequest {
    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
}
