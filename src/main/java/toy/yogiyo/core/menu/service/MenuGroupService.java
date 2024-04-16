package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;
    private final ShopRepository shopRepository;
    private final MenuService menuService;

    @Transactional
    public Long create(MenuGroup menuGroup) {
        if (shopRepository.findById(menuGroup.getShop().getId()).isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND);
        }

        Integer maxOrder = menuGroupRepository.findMaxOrder(menuGroup.getShop().getId());
        menuGroup.updatePosition(maxOrder == null ? 1 : maxOrder + 1);

        menuGroupRepository.save(menuGroup);
        return menuGroup.getId();
    }

    @Transactional(readOnly = true)
    public MenuGroup get(Long id) {
        return menuGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUGROUP_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MenuGroup> getMenuGroups(Long shopId) {
        return menuGroupRepository.findAllWithMenuByShopId(shopId);
    }

    @Transactional
    public void update(MenuGroup updateParam) {
        MenuGroup menuGroup = get(updateParam.getId());
        menuGroup.updateInfo(updateParam);
    }

    @Transactional
    public void delete(MenuGroup deleteParam) {
        MenuGroup menuGroup = get(deleteParam.getId());
        List<Menu> menus = menuService.getMenus(deleteParam.getId());

        menus.forEach(menuService::delete);
        menuGroupRepository.delete(menuGroup);
    }

    @Transactional
    public void updatePosition(Long shopId, List<MenuGroup> params) {
        List<MenuGroup> menuGroups = menuGroupRepository.findAllByShopId(shopId);

        IntStream.range(0, params.size())
                .forEach(i -> menuGroups.stream()
                        .filter(menuGroup -> Objects.equals(menuGroup.getId(), params.get(i).getId()))
                        .findFirst()
                        .ifPresent(menuGroup -> menuGroup.updatePosition(i + 1)));
    }

    @Transactional
    public void updateMenuPosition(Long menuGroupId, List<Menu> params) {
        List<Menu> menus = menuService.getMenus(menuGroupId);

        IntStream.range(0, params.size())
                .forEach(i -> menus.stream()
                        .filter(menu -> Objects.equals(menu.getId(), params.get(i).getId()))
                        .findFirst()
                        .ifPresent(menu -> menu.updatePosition(i + 1)));
    }

    @Transactional
    public void updateVisible(Long menuGroupId, Visible visible) {
        MenuGroup menuGroup = get(menuGroupId);
        menuGroup.updateVisible(visible);
    }
}
