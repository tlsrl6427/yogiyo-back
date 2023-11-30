package toy.yogiyo.core.shop.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import toy.yogiyo.common.dto.scroll.BaseScrollRequest;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@SuperBuilder
public class ShopScrollListRequest extends BaseScrollRequest {

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

    public ShopScrollListRequest(long offset, long limit, String category, String sortOption, Integer deliveryPrice, Integer leastOrderPrice, Double longitude, Double latitude) {
        super(offset, limit);
        this.category = category;
        this.sortOption = sortOption;
        this.deliveryPrice = deliveryPrice;
        this.leastOrderPrice = leastOrderPrice;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
