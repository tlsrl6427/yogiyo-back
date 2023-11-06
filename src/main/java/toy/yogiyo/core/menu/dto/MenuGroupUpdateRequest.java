package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.MenuGroup;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupUpdateRequest {

    private String name;
    private String content;

    public MenuGroup toMenuGroup() {
        return MenuGroup.builder()
                .name(name)
                .content(content)
                .build();
    }

    public MenuGroup toMenuGroup(Long menuGroupId) {
        return MenuGroup.builder()
                .id(menuGroupId)
                .name(name)
                .content(content)
                .build();
    }
}
