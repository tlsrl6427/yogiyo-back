package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.Menu;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
