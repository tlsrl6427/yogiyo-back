package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerShopResponse {
    private Long id;
    private String name;
    private String icon;

    public static OwnerShopResponse from(Shop shop) {
        return OwnerShopResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .icon(shop.getIcon())
                .build();
    }
}
