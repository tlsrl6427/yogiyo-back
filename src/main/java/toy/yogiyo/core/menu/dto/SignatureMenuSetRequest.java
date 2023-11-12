package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.shop.domain.Shop;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureMenuSetRequest {

    @NotNull
    private Long shopId;

    @NotNull
    @Builder.Default
    private List<Long> menuIds = new ArrayList<>();

    public List<SignatureMenu> toSignatureMenus() {
        return menuIds.stream()
                .map(menuId -> SignatureMenu.builder()
                        .menu(Menu.builder().id(menuId).build())
                        .shop(Shop.builder().id(shopId).build())
                        .build())
                .collect(Collectors.toList());
    }
}
