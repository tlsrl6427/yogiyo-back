package toy.yogiyo.core.shop.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

@Getter
@Builder
public class ShopNoticeResponse {

    private String title;
    private String notice;
    private List<String> images;

    public static ShopNoticeResponse from(Shop shop) {
        return ShopNoticeResponse.builder()
                .title(shop.getNoticeTitle())
                .notice(shop.getOwnerNotice())
                .images(shop.getNoticeImages())
                .build();
    }
}
