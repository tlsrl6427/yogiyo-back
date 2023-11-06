package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCreateRequest {

    private String name;
    private String content;
    private int price;

    public Menu toMenu(Long menuGroupId) {
        return Menu.builder()
                .name(name)
                .content(content)
                .price(price)
                .menuGroup(MenuGroup.builder().id(menuGroupId).build())
                .build();
    }
}
