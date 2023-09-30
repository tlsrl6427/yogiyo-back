package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
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

        public MenuGroupDto(MenuGroup menuGroup) {
            this.id = menuGroup.getId();
            this.name = menuGroup.getName();
            this.content = menuGroup.getContent();
        }

    }

}
