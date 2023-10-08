package toy.yogiyo.core.shop.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@Builder
public class ShopNoticeResponse {

    private String notice;

    public static ShopNoticeResponse from(Shop shop) {
        return ShopNoticeResponse.builder()
                .notice(shop.getOwnerNotice())
                .build();
    }
}
