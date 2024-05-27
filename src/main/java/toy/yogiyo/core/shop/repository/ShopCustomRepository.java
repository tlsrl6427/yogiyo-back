package toy.yogiyo.core.shop.repository;

import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.like.dto.LikeResponse;
import toy.yogiyo.core.like.dto.LikeScrollRequest;
import toy.yogiyo.core.shop.dto.ShopDetailsRequest;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopRecentRequest;
import toy.yogiyo.core.shop.dto.member.ShopInfoResponse;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListRequest;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollResponse;

import java.util.List;

public interface ShopCustomRepository {
    List<LikeResponse> scrollLikes(Long memberId, LikeScrollRequest request);
    List<ShopScrollResponse> scrollShopList(ShopScrollListRequest request);

    ShopDetailsResponse details(Long memberId, ShopDetailsRequest request);

    ShopInfoResponse info(Long shopId, String code);

    List<ShopScrollResponse> recentOrder(Long memberId, ShopRecentRequest request);

//    List<ShopScrollResponse> getSearchShops(ShopScrollListRequest request);
}
