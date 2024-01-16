package toy.yogiyo.core.shop.dto;

import lombok.*;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollResponse;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NewShopListResponse {
    private List<ShopScrollResponse> newShops;
}
