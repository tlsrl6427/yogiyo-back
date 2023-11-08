package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionGroupUpdatePositionRequest {

    @NotNull
    private List<Long> menuOptionGroupIds;

    public List<MenuOptionGroup> toMenuOptionGroups() {
        return menuOptionGroupIds.stream()
                .map(id -> MenuOptionGroup.builder()
                        .id(id)
                        .build())
                .collect(Collectors.toList());
    }
}
