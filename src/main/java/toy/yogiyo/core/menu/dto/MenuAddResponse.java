package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menu.domain.MenuGroupItem;

@Getter
@Builder
public class MenuAddResponse {

    private Long id;

    public static MenuAddResponse from(MenuGroupItem menuGroupItem) {
        return MenuAddResponse.builder()
                .id(menuGroupItem.getMenu().getId())
                .build();
    }
}
