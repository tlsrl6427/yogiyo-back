package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.yogiyo.core.menu.dto.member.MenuDetailsGetResponse;
import toy.yogiyo.core.menu.repository.MenuQueryRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/menu")
public class MenuController {

    private final MenuQueryRepository menuQueryRepository;

    @GetMapping("/{menuId}")
    public MenuDetailsGetResponse getDetails(@PathVariable Long menuId) {
        return menuQueryRepository.getDetails(menuId);
    }
}
