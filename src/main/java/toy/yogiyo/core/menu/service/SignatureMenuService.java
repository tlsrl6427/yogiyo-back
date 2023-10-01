package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
}
