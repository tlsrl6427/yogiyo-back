package toy.yogiyo.core.menuoption.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.repository.MenuOptionGroupRepository;

@Service
@RequiredArgsConstructor
public class MenuOptionGroupService {

    private final MenuOptionGroupRepository menuOptionGroupRepository;
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
    public MenuOptionGroup findWithMenuOptions(Long menuOptionGroupId) {
        return menuOptionGroupRepository.findWithMenuOptionById(menuOptionGroupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUOPTIONGROUP_NOT_FOUND));
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

}
