package toy.yogiyo.core.menu.dto;

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
public class MenuGroupChangeMenuOrderRequest {

    List<Long> menuIds;

    public List<Menu> toEntity() {
        return menuIds.stream()
                .map(menuId -> Menu.builder()
                        .id(menuId)
                        .build())
                .collect(Collectors.toList());
    }

}
