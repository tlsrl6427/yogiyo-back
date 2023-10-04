package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@Builder
public class MenuGroupAddRequest {

    private Long shopId;
    private String name;
    private String content;
    public MenuGroup toEntity() {
        return MenuGroup.builder()
                .shop(Shop.builder().id(shopId).build())
                .name(name)
                .content(content)
                .build();
    }
}
