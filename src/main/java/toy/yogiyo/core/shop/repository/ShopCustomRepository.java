package toy.yogiyo.core.shop.repository;

import toy.yogiyo.core.like.dto.LikeResponse;
import toy.yogiyo.core.like.dto.LikeScrollRequest;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListRequest;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollResponse;

import java.util.List;

public interface ShopCustomRepository {
    List<LikeResponse> scrollLikes(Long memberId, LikeScrollRequest request);
    List<ShopScrollResponse> scrollShopList(ShopScrollListRequest request);
}
