package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionGroupChangeOrderRequest {

    private List<Long> menuOptionGroupIds;

    public List<MenuOptionGroup> toEntity() {
        return menuOptionGroupIds.stream()
                .map(id -> MenuOptionGroup.builder()
                        .id(id)
                        .build())
                .collect(Collectors.toList());
    }
}
