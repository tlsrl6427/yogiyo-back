package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.Menu;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupUpdateMenuPositionRequest {

    @NotNull
    List<Long> menuIds;

    public List<Menu> toMenus() {
        return menuIds.stream()
                .map(menuId -> Menu.builder()
                        .id(menuId)
                        .build())
                .collect(Collectors.toList());
    }

}
