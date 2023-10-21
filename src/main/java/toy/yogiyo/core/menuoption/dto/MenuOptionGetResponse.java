package toy.yogiyo.core.menuoption.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menuoption.domain.MenuOption;

@Getter
@Builder
public class MenuOptionGetResponse {

    private Long id;
    private String content;
    private int price;

    public static MenuOptionGetResponse from(MenuOption menuOption) {
        return MenuOptionGetResponse.builder()
                .id(menuOption.getId())
                .content(menuOption.getContent())
                .price(menuOption.getPrice())
                .build();
    }
}
