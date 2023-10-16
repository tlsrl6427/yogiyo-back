package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;
    private final MenuService menuService;

    // =================== 점주 기능 ======================
    @Transactional
    public Long add(MenuGroup menuGroup) {
        menuGroupRepository.save(menuGroup);
        return menuGroup.getId();
    }

    @Transactional(readOnly = true)
    public MenuGroup find(Long id) {
        return menuGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUGROUP_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MenuGroup> findMenuGroups(Long shopId) {
        return menuGroupRepository.findAllByShopId(shopId);
    }

    @Transactional
    public void update(MenuGroup updateParam) {
        MenuGroup menuGroup = find(updateParam.getId());
        menuGroup.changeInfo(updateParam);
    }

    @Transactional
    public void delete(MenuGroup deleteParam) {
        MenuGroup menuGroup = find(deleteParam.getId());
        List<Menu> menus = menuService.findMenus(deleteParam.getId());

        menus.forEach(menuService::delete);
        menuGroupRepository.delete(menuGroup);
    }

    @Transactional
    public void changeMenuOrder(Long menuGroupId, List<Menu> params) {
        List<Menu> menus = menuService.findMenus(menuGroupId);

        IntStream.range(0, params.size())
                .forEach(i -> menus.stream()
                        .filter(menu -> Objects.equals(menu.getId(), params.get(i).getId()))
                        .findFirst()
                        .ifPresent(menu -> menu.changePosition(i + 1)));
    }


    // =================== 고객 기능 ======================
}
