package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionGroupUpdateRequest {

    private String name;

    public MenuOptionGroup toEntity(Long menuOptionGroupId) {
        return MenuOptionGroup.builder()
                .id(menuOptionGroupId)
                .name(name)
                .build();
    }
}
