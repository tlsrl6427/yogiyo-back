package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menu.domain.MenuGroup;

@Getter
@Builder
public class MenuGroupAddResponse {

    private Long id;

    public static MenuGroupAddResponse from(MenuGroup menuGroup) {
        return MenuGroupAddResponse.builder()
                .id(menuGroup.getId())
                .build();
    }
}
