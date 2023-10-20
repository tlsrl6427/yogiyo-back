package toy.yogiyo.core.menuoption.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;

@Getter
@Builder
public class MenuOptionGroupGetResponse {

    private Long id;
    private String name;

    public static MenuOptionGroupGetResponse from(MenuOptionGroup menuOptionGroup) {
        return MenuOptionGroupGetResponse.builder()
                .id(menuOptionGroup.getId())
                .name(menuOptionGroup.getName())
                .build();
    }

}
