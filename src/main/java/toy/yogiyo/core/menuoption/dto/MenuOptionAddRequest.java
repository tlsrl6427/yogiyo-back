package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionAddRequest {

    private String content;
    private int price;

    public MenuOption toEntity(Long optionGroupId) {
        return MenuOption.builder()
                .menuOptionGroup(MenuOptionGroup.builder().id(optionGroupId).build())
                .content(content)
                .price(price)
                .build();
    }
}
