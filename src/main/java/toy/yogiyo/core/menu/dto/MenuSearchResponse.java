package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.core.menu.domain.Menu;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuSearchResponse {

    private List<MenuDto> menus;

    @Getter
    public static class MenuDto {
        private Long id;
        private String name;
        private String content;
        private int price;
        private String picture;
        private Visible visible;
    }
}
