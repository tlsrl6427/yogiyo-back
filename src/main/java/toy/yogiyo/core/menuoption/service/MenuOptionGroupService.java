package toy.yogiyo.core.menuoption.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.repository.MenuRepository;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.repository.MenuOptionGroupRepository;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MenuOptionGroupService {

    private final MenuOptionGroupRepository menuOptionGroupRepository;
    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public Long create(MenuOptionGroup menuOptionGroup) {
        if (shopRepository.findById(menuOptionGroup.getShop().getId()).isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND);
        }

        Integer maxOrder = menuOptionGroupRepository.findMaxOrder(menuOptionGroup.getShop().getId());
        menuOptionGroup.updatePosition(maxOrder == null ? 1 : maxOrder + 1);

        List<MenuOption> menuOptions = menuOptionGroup.getMenuOptions();
        for (int i = 0; i < menuOptions.size(); i++) {
            menuOptions.get(i).updatePosition(i + 1);
        }

        menuOptionGroupRepository.save(menuOptionGroup);

        return menuOptionGroup.getId();
    }

    @Transactional(readOnly = true)
    public MenuOptionGroup get(Long menuOptionGroupId) {
        return menuOptionGroupRepository.findById(menuOptionGroupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUOPTIONGROUP_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MenuOptionGroup> getAll(Long shopId) {
        return menuOptionGroupRepository.findAllWithOptionByShopId(shopId);
    }

    @Transactional
    public void update(MenuOptionGroup updateParam) {
        MenuOptionGroup menuOptionGroup = get(updateParam.getId());
        menuOptionGroup.updateName(updateParam.getName());
    }

    @Transactional
    public void delete(Long menuOptionGroupId) {
        MenuOptionGroup menuOptionGroup = get(menuOptionGroupId);
        menuOptionGroupRepository.delete(menuOptionGroup);
    }

    @Transactional
    public void linkMenu(Long menuOptionGroupId, List<Menu> menus) {
        menus.forEach(menu -> {
            if (menuRepository.findById(menu.getId()).isEmpty()) {
                throw new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND);
            }});

        MenuOptionGroup menuOptionGroup = get(menuOptionGroupId);
        menuOptionGroup.updateLinkMenus(menus);
    }

    @Transactional
    public void updatePosition(Long shopId, List<MenuOptionGroup> params) {
        List<MenuOptionGroup> optionGroups = menuOptionGroupRepository.findAllByShopId(shopId);

        IntStream.range(0, params.size())
                .forEach(i -> optionGroups.stream()
                        .filter(optionGroup -> Objects.equals(optionGroup.getId(), params.get(i).getId()))
                        .findFirst()
                        .ifPresent(optionGroup -> optionGroup.updatePosition(i + 1)));
    }

    @Transactional
    public void updateVisible(Long menuOptionGroupId, Visible visible) {
        MenuOptionGroup menuOptionGroup = get(menuOptionGroupId);
        menuOptionGroup.updateVisible(visible);
    }

}
