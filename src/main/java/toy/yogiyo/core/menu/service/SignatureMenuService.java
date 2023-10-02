package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.menu.repository.SignatureMenuRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignatureMenuService {

    private final SignatureMenuRepository signatureMenuRepository;

    @Transactional
    public Long create(SignatureMenu signatureMenu) {
        Integer maxOrder = signatureMenuRepository.findMaxOrder(signatureMenu.getShop().getId());
        signatureMenu.changePosition(maxOrder == null ? 1 : maxOrder + 1);

        signatureMenuRepository.save(signatureMenu);
        return signatureMenu.getId();
    }

    @Transactional(readOnly = true)
    public List<SignatureMenu> findAll(Long shopId) {
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
    public void changeMenuOrder(List<SignatureMenu> params) {
        for (int i = 0; i < params.size(); i++) {
            SignatureMenu signatureMenu = signatureMenuRepository.findByMenuId(params.get(i).getMenu().getId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SIGNATUREMENU_NOT_FOUND));

            signatureMenu.changePosition(i + 1);
        }
    }
}
