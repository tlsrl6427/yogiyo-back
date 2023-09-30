package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menu.domain.MenuGroup;

@Getter
@Builder
public class MenuGroupUpdateRequest {

    private String name;
    private String content;

    public MenuGroup toEntity() {
        return MenuGroup.builder()
                .name(name)
                .content(content)
                .build();
    }

    public MenuGroup toEntity(Long menuGroupId) {
        return MenuGroup.builder()
                .id(menuGroupId)
                .name(name)
                .content(content)
                .build();
    }
}
