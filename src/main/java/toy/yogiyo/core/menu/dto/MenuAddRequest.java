package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@Builder
public class MenuAddRequest {

    private String name;
    private String content;
    private int price;
    private Long shopId;

    public Menu toEntity() {
        return Menu.builder()
                .name(name)
                .content(content)
                .price(price)
                .shop(Shop.builder().id(shopId).build())
                .build();
    }
}
