package toy.yogiyo.core.menuoption.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.repository.MenuOptionGroupRepository;
import toy.yogiyo.core.menuoption.repository.MenuOptionRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MenuOptionService {

    private final MenuOptionRepository menuOptionRepository;
    private final MenuOptionGroupRepository menuOptionGroupRepository;

    @Transactional
    public Long create(MenuOption menuOption) {
        if (menuOptionGroupRepository.findById(menuOption.getMenuOptionGroup().getId()).isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.MENUOPTIONGROUP_NOT_FOUND);
        }

        Integer maxOrder = menuOptionRepository.findMaxOrder(menuOption.getMenuOptionGroup().getId());
        menuOption.updatePosition(maxOrder == null ? 1 : maxOrder + 1);

        menuOptionRepository.save(menuOption);
        return menuOption.getId();
    }

    @Transactional(readOnly = true)
    public MenuOption get(Long menuOptionId) {
        return menuOptionRepository.findById(menuOptionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUOPTION_NOT_FOUND));
    }

    @Transactional
    public void update(MenuOption updateParam) {
        MenuOption menuOption = get(updateParam.getId());
        menuOption.updateInfo(updateParam);
    }

    @Transactional
    public void delete(Long menuOptionId) {
        MenuOption menuOption = get(menuOptionId);
        menuOptionRepository.delete(menuOption);
    }

    @Transactional
    public int deleteAll(Long menuOptionGroupId) {
        return menuOptionRepository.deleteAllByGroupId(menuOptionGroupId);
    }

    @Transactional
    public void updatePosition(Long menuOptionGroupId, List<MenuOption> params) {
        List<MenuOption> options = menuOptionRepository.findAllByMenuOptionGroupId(menuOptionGroupId);

        IntStream.range(0, params.size())
                .forEach(i -> options.stream()
                        .filter(option -> Objects.equals(option.getId(), params.get(i).getId()))
                        .findFirst()
                        .ifPresent(option -> option.updatePosition(i + 1)));
    }

    @Transactional
    public void updateVisible(Long menuOptionId, Visible visible) {
        MenuOption menuOption = get(menuOptionId);
        menuOption.updateVisible(visible);
    }

}
