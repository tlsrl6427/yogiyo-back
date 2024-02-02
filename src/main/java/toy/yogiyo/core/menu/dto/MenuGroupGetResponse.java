package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.core.menu.domain.MenuGroup;

@Getter
@Builder
public class MenuGroupGetResponse {

    private Long id;
    private String name;
    private String content;
    private Visible visible;

    public static MenuGroupGetResponse from(MenuGroup menuGroup) {
        return MenuGroupGetResponse.builder()
                .id(menuGroup.getId())
                .name(menuGroup.getName())
                .content(menuGroup.getContent())
                .visible(menuGroup.getVisible())
                .build();
    }

}
