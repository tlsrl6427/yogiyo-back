package toy.yogiyo.core.menuoption.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.repository.MenuOptionRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MenuOptionService {

    private final MenuOptionRepository menuOptionRepository;

    @Transactional
    public Long add(MenuOption menuOption) {
        Integer maxOrder = menuOptionRepository.findMaxOrder(menuOption.getMenuOptionGroup().getId());
        menuOption.changePosition(maxOrder == null ? 1 : maxOrder + 1);

        menuOptionRepository.save(menuOption);
        return menuOption.getId();
    }

    @Transactional(readOnly = true)
    public MenuOption find(Long menuOptionId) {
        return menuOptionRepository.findById(menuOptionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENUOPTION_NOT_FOUND));
    }

    @Transactional
    public void update(MenuOption updateParam) {
        MenuOption menuOption = find(updateParam.getId());
        menuOption.changeInfo(updateParam);
    }

    @Transactional
    public void delete(Long menuOptionId) {
        MenuOption menuOption = find(menuOptionId);
        menuOptionRepository.delete(menuOption);
    }

    @Transactional
    public int deleteAll(Long menuOptionGroupId) {
        return menuOptionRepository.deleteAllByGroupId(menuOptionGroupId);
    }

    @Transactional
    public void changeOrder(Long menuOptionGroupId, List<MenuOption> params) {
        List<MenuOption> options = menuOptionRepository.findAllByMenuOptionGroupId(menuOptionGroupId);

        IntStream.range(0, params.size())
                .forEach(i -> options.stream()
                        .filter(option -> Objects.equals(option.getId(), params.get(i).getId()))
                        .findFirst()
                        .ifPresent(option -> option.changePosition(i + 1)));
    }

}
