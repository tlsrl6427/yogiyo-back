package toy.yogiyo.api;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopScrollListResponse {

    private List<ShopScrollResponse> shopScrollResponses;
    private Long nextOffset;
    private boolean hasNext;
}
