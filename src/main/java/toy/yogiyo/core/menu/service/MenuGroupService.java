package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;


@Service
@RequiredArgsConstructor
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;

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
}
