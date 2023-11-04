package toy.yogiyo.core.shop.repository;

import com.querydsl.core.Tuple;
import toy.yogiyo.api.ShopScrollListRequest;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

public interface ShopCustomRepository {
    List<Shop> scrollLikes(Long memberId, Long lastId);
    List<Shop> scrollShopList(ShopScrollListRequest request);
}
