package toy.yogiyo.core.shop.repository;

import toy.yogiyo.core.like.dto.LikeResponse;
import toy.yogiyo.core.like.dto.LikeScrollRequest;
import toy.yogiyo.core.shop.dto.NewShopListRequest;
import toy.yogiyo.core.shop.dto.ShopScrollListRequest;
import toy.yogiyo.core.shop.dto.ShopScrollResponse;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

public interface ShopCustomRepository {
    List<LikeResponse> scrollLikes(Long memberId, LikeScrollRequest request);
    List<ShopScrollResponse> scrollShopList(ShopScrollListRequest request);
}
