package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menu.domain.MenuGroup;

@Getter
@Builder
public class MenuGroupCreateResponse {

    private Long id;

    public static MenuGroupCreateResponse from(MenuGroup menuGroup) {
        return MenuGroupCreateResponse.builder()
                .id(menuGroup.getId())
                .build();
    }
}
