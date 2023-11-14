package toy.yogiyo.core.shop.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopScrollListRequest {

    private String category;
    private String sortOption;
    @PositiveOrZero
    private Integer deliveryPrice;
    @PositiveOrZero
    private Integer leastOrderPrice;
    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
    @NotNull
    @PositiveOrZero
    private Long offset;
}
