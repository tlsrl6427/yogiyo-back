package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupCreateRequest {

    private Long shopId;
    private String name;
    private String content;
    public MenuGroup toMenuGroup() {
        return MenuGroup.builder()
                .shop(Shop.builder().id(shopId).build())
                .name(name)
                .content(content)
                .build();
    }
}
