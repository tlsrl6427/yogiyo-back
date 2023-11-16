package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.MenuGroup;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupUpdatePositionRequest {

    @NotNull
    List<Long> menuGroupIds;

    public List<MenuGroup> toMenuGroups() {
        return menuGroupIds.stream()
                .map(menuGroupId -> MenuGroup.builder()
                        .id(menuGroupId)
                        .build())
                .collect(Collectors.toList());
    }

}
