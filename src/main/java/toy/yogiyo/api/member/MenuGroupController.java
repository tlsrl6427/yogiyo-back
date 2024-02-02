package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.dto.member.MenuGroupsGetResponse;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;

import java.util.List;

@RestController
@RequestMapping("/member/menu-group")
@RequiredArgsConstructor
public class MenuGroupController {

    private final MenuGroupRepository menuGroupRepository;

    @GetMapping("/shop/{shopId}")
    public MenuGroupsGetResponse getMenuGroups(@PathVariable Long shopId) {
        List<MenuGroup> menuGroups = menuGroupRepository.findAllSellableWithMenuByShopId(shopId);
        return MenuGroupsGetResponse.from(menuGroups);
    }
}
