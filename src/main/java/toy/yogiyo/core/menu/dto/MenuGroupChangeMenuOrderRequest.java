package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroupItem;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupChangeMenuOrderRequest {

    List<Long> menuIds;

    public List<MenuGroupItem> toEntity() {
        return menuIds.stream()
                .map(menuId -> MenuGroupItem.builder()
                        .menu(Menu.builder().id(menuId).build())
                        .build())
                .collect(Collectors.toList());
    }

}
