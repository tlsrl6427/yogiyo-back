package toy.yogiyo.core.shop.repository;

import toy.yogiyo.core.shop.dto.NewShopListRequest;
import toy.yogiyo.core.shop.dto.ShopScrollListRequest;
import toy.yogiyo.core.shop.dto.ShopScrollResponse;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

public interface ShopCustomRepository {
    List<Shop> scrollLikes(Long memberId, Long lastId);
    List<ShopScrollResponse> scrollShopList(ShopScrollListRequest request);
}
