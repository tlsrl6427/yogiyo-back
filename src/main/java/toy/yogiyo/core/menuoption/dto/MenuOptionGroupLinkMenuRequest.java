package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.Menu;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionGroupLinkMenuRequest {

    private List<Long> menuIds;

    public List<Menu> toMenus() {
        return menuIds.stream()
                .map(id -> Menu.builder()
                        .id(id)
                        .build())
                .collect(Collectors.toList());
    }
}
