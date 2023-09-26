package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.domain.MenuGroupItem;
import toy.yogiyo.core.menu.repository.MenuGroupItemRepository;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;
    private final MenuGroupItemRepository menuGroupItemRepository;
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
        menuGroupRepository.delete(menuGroup);
    }

    @Transactional
    public Long addMenu(MenuGroupItem menuGroupItem) {
        Integer maxOrder = menuGroupItemRepository.findMaxOrder(menuGroupItem.getMenuGroup().getId());
        menuGroupItem.changePosition(maxOrder == null ? 1 : maxOrder + 1);

        menuGroupItemRepository.save(menuGroupItem);
        menuService.add(menuGroupItem.getMenu());

        return menuGroupItem.getId();
    }

    @Transactional
    public void deleteMenu(MenuGroupItem deleteParam) {
        MenuGroupItem menuGroupItem = menuGroupItemRepository.findByMenuId(deleteParam.getMenu().getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUGROUPITEM_NOT_FOUND));

        menuService.delete(menuGroupItem.getMenu());
        menuGroupItemRepository.delete(menuGroupItem);
    }

    @Transactional(readOnly = true)
    public List<MenuGroupItem> findMenus(Long menuGroupId) {
        return menuGroupItemRepository.findMenus(menuGroupId);
    }

    @Transactional
    public void changeMenuOrder(List<MenuGroupItem> params) {
        for (MenuGroupItem param : params) {
            MenuGroupItem menuGroupItem = menuGroupItemRepository.findByMenuId(param.getMenu().getId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUGROUPITEM_NOT_FOUND));

            menuGroupItem.changePosition(param.getPosition());
        }
    }


    // =================== 고객 기능 ======================
}
