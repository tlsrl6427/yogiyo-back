package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MenuGroupsGetResponse {

    private List<MenuGroupDto> menuGroups;

    public static MenuGroupsGetResponse from(List<MenuGroup> menuGroups) {
        return MenuGroupsGetResponse.builder()
                .menuGroups(menuGroups.stream()
                        .map(MenuGroupDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    private static class MenuGroupDto {

        private Long id;
        private String name;
        private String content;
        private Visible visible;
        private List<MenuDto> menus;

        public MenuGroupDto(MenuGroup menuGroup) {
            this.id = menuGroup.getId();
            this.name = menuGroup.getName();
            this.content = menuGroup.getContent();
            this.visible = menuGroup.getVisible();
            this.menus = menuGroup.getMenus().stream()
                    .map(MenuDto::new)
                    .collect(Collectors.toList());
        }

    }

    @Getter
    private static class MenuDto {
        private Long id;
        private String name;
        private String content;
        private int price;
        private String picture;
        private Visible visible;

        public MenuDto(Menu menu) {
            this.id = menu.getId();
            this.name = menu.getName();
            this.content = menu.getContent();
            this.price = menu.getPrice();
            this.picture = menu.getPicture();
            this.visible = menu.getVisible();
        }
    }

}
