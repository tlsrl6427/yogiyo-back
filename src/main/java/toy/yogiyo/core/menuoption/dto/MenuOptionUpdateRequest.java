package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOption;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionUpdateRequest {

    @NotBlank
    private String content;
    @Min(0)
    private int price;

    public MenuOption toMenuOption(Long menuOptionId) {
        return MenuOption.builder()
                .id(menuOptionId)
                .content(content)
                .price(price)
                .build();
    }
}
