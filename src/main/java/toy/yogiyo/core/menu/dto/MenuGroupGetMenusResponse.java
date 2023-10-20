package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menu.domain.Menu;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MenuGroupGetMenusResponse {

    private List<MenuDto> menus;

    public static MenuGroupGetMenusResponse from(List<Menu> menus) {
        return MenuGroupGetMenusResponse.builder()
                .menus(menus.stream()
                        .map(MenuDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    private static class MenuDto {

        private Long id;
        private String name;
        private String content;
        private String picture;
        private int price;

        public MenuDto(Menu menu) {
            this.id = menu.getId();
            this.name = menu.getName();
            this.content = menu.getContent();
            this.picture = menu.getPicture();
            this.price = menu.getPrice();
        }
    }
}
