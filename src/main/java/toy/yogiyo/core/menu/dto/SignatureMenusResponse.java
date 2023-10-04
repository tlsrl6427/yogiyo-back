package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.SignatureMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureMenusResponse {

    @Builder.Default
    List<MenuDto> signatureMenus = new ArrayList<>();

    public static SignatureMenusResponse from(List<SignatureMenu> signatureMenus) {
        return SignatureMenusResponse.builder()
                .signatureMenus(signatureMenus.stream()
                        .map(signatureMenu -> new MenuDto(signatureMenu.getMenu()))
                        .collect(Collectors.toList()))
                .build();
    }


    @Getter
    private static class MenuDto {

        private Long id;
        private String name;
        private String content;
        private String picture;
        private int price;

        public MenuDto (Menu menu) {
            this.id = menu.getId();
            this.name = menu.getName();
            this.content = menu.getContent();
            this.picture = menu.getPicture();
            this.price = menu.getPrice();
        }
    }
}
