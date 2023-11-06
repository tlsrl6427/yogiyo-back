package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOption;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionUpdateRequest {

    private String content;
    private int price;

    public MenuOption toMenuOption(Long menuOptionId) {
        return MenuOption.builder()
                .id(menuOptionId)
                .content(content)
                .price(price)
                .build();
    }
}
