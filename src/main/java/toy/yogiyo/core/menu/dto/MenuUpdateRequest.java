package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.core.menu.domain.Menu;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuUpdateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @Min(0)
    private int price;
    @NotNull
    private Visible visible;

    public Menu toMenu(Long menuId) {
        return Menu.builder()
                .id(menuId)
                .name(name)
                .content(content)
                .price(price)
                .visible(visible)
                .build();
    }
}
