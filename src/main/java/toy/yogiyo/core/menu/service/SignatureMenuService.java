package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.menu.repository.SignatureMenuRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SignatureMenuService {

    private final SignatureMenuRepository signatureMenuRepository;

    @Transactional
    public Long create(SignatureMenu signatureMenu) {
        Integer maxOrder = signatureMenuRepository.findMaxOrder(signatureMenu.getShop().getId());
        signatureMenu.updatePosition(maxOrder == null ? 1 : maxOrder + 1);

        signatureMenuRepository.save(signatureMenu);
        return signatureMenu.getId();
    }

    @Transactional(readOnly = true)
    public List<SignatureMenu> getAll(Long shopId) {
        return signatureMenuRepository.findAlLByShopId(shopId);
    }

    @Transactional
    public int deleteAll(Long shopId) {
        return signatureMenuRepository.deleteAllByShopId(shopId);
    }

    @Transactional
    public void delete(Long menuId) {
        SignatureMenu signatureMenu = signatureMenuRepository.findByMenuId(menuId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SIGNATUREMENU_NOT_FOUND));

        signatureMenuRepository.delete(signatureMenu);
    }


    @Transactional
    public void updateMenuPosition(Long shopId, List<SignatureMenu> params) {

        List<SignatureMenu> signatureMenus = signatureMenuRepository.findAlLByShopId(shopId);

        IntStream.range(0, params.size())
                .forEach(i -> signatureMenus.stream()
                        .filter(signatureMenu -> Objects.equals(signatureMenu.getMenu().getId(), params.get(i).getMenu().getId()))
                        .findFirst()
                        .ifPresent(signatureMenu -> signatureMenu.updatePosition(i + 1)));
    }
}
