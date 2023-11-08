package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionGroupUpdateRequest {

    @NotBlank
    private String name;

    public MenuOptionGroup toMenuOptionGroup(Long menuOptionGroupId) {
        return MenuOptionGroup.builder()
                .id(menuOptionGroupId)
                .name(name)
                .build();
    }
}
