package toy.yogiyo.api;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopScrollListRequest {

    private String category;
    private String sortOption;
    private Integer deliveryPrice;
    private Integer leastOrderPrice;
    private Double longitude;
    private Double latitude;
    private Long offset;
}
