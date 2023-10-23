package toy.yogiyo.core.shop.repository;

import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

public interface ShopCustomRepository {
    List<Shop> scrollLikes(Long memberId, Long lastId);
}
