package toy.yogiyo.core.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.core.menu.domain.MenuGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupUpdateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @NotNull
    private Visible visible;

    public MenuGroup toMenuGroup() {
        return MenuGroup.builder()
                .name(name)
                .content(content)
                .build();
    }

    public MenuGroup toMenuGroup(Long menuGroupId) {
        return MenuGroup.builder()
                .id(menuGroupId)
                .name(name)
                .content(content)
                .visible(visible)
                .build();
    }
}
