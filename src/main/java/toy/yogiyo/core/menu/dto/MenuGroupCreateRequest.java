package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.shop.domain.Shop;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupCreateRequest {

    @NotNull
    private Long shopId;
    @NotBlank
    private String name;
    @NotBlank
    private String content;
    public MenuGroup toMenuGroup() {
        return MenuGroup.builder()
                .shop(Shop.builder().id(shopId).build())
                .name(name)
                .content(content)
                .build();
    }
}
