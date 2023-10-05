package toy.yogiyo.core.menuoption.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroupMenu;
import toy.yogiyo.core.menuoption.repository.MenuOptionGroupMenuRepository;
import toy.yogiyo.core.menuoption.repository.MenuOptionGroupRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MenuOptionGroupService {

    private final MenuOptionGroupRepository menuOptionGroupRepository;
    private final MenuOptionGroupMenuRepository menuOptionGroupMenuRepository;
    private final MenuOptionService menuOptionService;

    @Transactional
    public Long add(MenuOptionGroup menuOptionGroup) {
        Integer maxOrder = menuOptionGroupRepository.findMaxOrder(menuOptionGroup.getShop().getId());
        menuOptionGroup.changePosition(maxOrder == null ? 1 : maxOrder + 1);
        menuOptionGroupRepository.save(menuOptionGroup);

        for (MenuOption menuOption : menuOptionGroup.getMenuOptions()) {
            menuOptionService.add(menuOption);
        }

        return menuOptionGroup.getId();
    }

    @Transactional(readOnly = true)
    public MenuOptionGroup find(Long menuOptionGroupId) {
        return menuOptionGroupRepository.findById(menuOptionGroupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUOPTIONGROUP_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MenuOptionGroup> findAll(Long shopId) {
        List<MenuOptionGroup> optionGroups = menuOptionGroupRepository.findAllByShopId(shopId);

        optionGroups.forEach(optionGroup ->
                optionGroup.getMenuOptions().sort((o1, o2) -> {
                    if (o1.getPosition() > o2.getPosition()) {
                        return 1;
                    } else if (o1.getPosition() < o2.getPosition()) {
                        return -1;
                    }
                    return 0;
                })
        );

        return optionGroups;
    }

    @Transactional
    public void update(MenuOptionGroup updateParam) {
        MenuOptionGroup menuOptionGroup = find(updateParam.getId());
        menuOptionGroup.changeName(updateParam.getName());
    }

    @Transactional
    public void delete(Long menuOptionGroupId) {
        MenuOptionGroup menuOptionGroup = find(menuOptionGroupId);
        menuOptionService.deleteAll(menuOptionGroupId);
        menuOptionGroupRepository.delete(menuOptionGroup);
    }

    @Transactional
    public void linkMenu(Long menuOptionGroupId, List<Menu> menus) {
        MenuOptionGroup menuOptionGroup = find(menuOptionGroupId);

        List<MenuOptionGroupMenu> menuOptionGroupMenu = menus.stream()
                .map(menu -> MenuOptionGroupMenu.builder()
                        .menuOptionGroup(menuOptionGroup)
                        .menu(menu)
                        .build())
                .collect(Collectors.toList());

        menuOptionGroupMenuRepository.deleteByMenuOptionGroupId(menuOptionGroupId);
        menuOptionGroupMenuRepository.saveAll(menuOptionGroupMenu);
    }

    @Transactional
    public void changeOrder(Long shopId, List<MenuOptionGroup> params) {
        List<MenuOptionGroup> optionGroups = menuOptionGroupRepository.findAllByShopId(shopId);

        IntStream.range(0, params.size())
                .forEach(i -> optionGroups.stream()
                        .filter(optionGroup -> Objects.equals(optionGroup.getId(), params.get(i).getId()))
                        .findFirst()
                        .ifPresent(optionGroup -> optionGroup.changePosition(i + 1)));
    }

}
