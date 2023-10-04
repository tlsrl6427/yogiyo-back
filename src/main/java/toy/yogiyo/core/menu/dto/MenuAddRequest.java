package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menu.domain.Menu;

@Getter
@Builder
public class MenuAddRequest {

    private String name;
    private String content;
    private int price;

    public Menu toEntity() {
        return Menu.builder()
                .name(name)
                .content(content)
                .price(price)
                .build();
    }
}
