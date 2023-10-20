package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOption;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionChangeOrderRequest {

    private List<Long> menuOptionIds;

    public List<MenuOption> toEntity() {
        return menuOptionIds.stream()
                .map(id -> MenuOption.builder()
                        .id(id)
                        .build())
                .collect(Collectors.toList());
    }
}
