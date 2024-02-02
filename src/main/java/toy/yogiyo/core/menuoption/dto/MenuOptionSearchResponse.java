package toy.yogiyo.core.menuoption.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.core.menuoption.domain.MenuOption;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MenuOptionSearchResponse {
    private List<MenuOptionDto> menuOptions;

    public static MenuOptionSearchResponse from(List<MenuOption> menuOptions) {
        return MenuOptionSearchResponse.builder()
                .menuOptions(menuOptions.stream()
                        .map(MenuOptionDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    private static class MenuOptionDto {

        private Long id;
        private String content;
        private Integer price;
        private Integer position;
        private Visible visible;

        public MenuOptionDto(MenuOption menuOption) {
            this.id = menuOption.getId();
            this.content = menuOption.getContent();
            this.price = menuOption.getPrice();
            this.position = menuOption.getPosition();
            this.visible = menuOption.getVisible();
        }

    }
}
