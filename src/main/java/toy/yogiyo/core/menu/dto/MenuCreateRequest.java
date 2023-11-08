package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCreateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @Min(0)
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
