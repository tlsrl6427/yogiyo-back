package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;
import toy.yogiyo.core.menu.repository.MenuRepository;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ImageFileHandler imageFileHandler;
    private final ShopRepository shopRepository;

    @Transactional
    public Long create(Menu menu, MultipartFile picture) {
        if(menuGroupRepository.findById(menu.getMenuGroup().getId()).isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.MENUGROUP_NOT_FOUND);
        }

        Integer maxOrder = menuRepository.findMaxOrder(menu.getMenuGroup().getId());
        menu.updatePosition(maxOrder == null ? 1 : maxOrder + 1);

        if (picture != null && !picture.isEmpty()) {
            menu.updatePicture(ImageFileUtil.getFilePath(imageFileHandler.store(picture)));
        }

        menuRepository.save(menu);
        return menu.getId();
    }

    @Transactional(readOnly = true)
    public Menu get(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Menu> getMenus(Long menuGroupId) {
        return menuRepository.findMenus(menuGroupId);
    }

    @Transactional
    public void update(Menu updateParam, MultipartFile picture) {
        Menu menu = get(updateParam.getId());

        if(picture != null) {
            if(hasPicture(menu.getPicture()) && !imageFileHandler.remove(ImageFileUtil.extractFilename(menu.getPicture()))){
                throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
            }

            menu.updatePicture(ImageFileUtil.getFilePath(imageFileHandler.store(picture)));
        }
        menu.updateInfo(updateParam);
    }

    @Transactional
    public void delete(Menu param) {
        Menu menu = get(param.getId());

        if(hasPicture(menu.getPicture()) && !imageFileHandler.remove(ImageFileUtil.extractFilename(menu.getPicture()))){
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }

        menuRepository.delete(menu);
    }


    @Transactional
    public void updateVisible(Long menuId, Visible visible) {
        Menu menu = get(menuId);
        LocalDateTime soldOutUntil = null;

        if(visible == Visible.SOLD_OUT) {
            soldOutUntil = shopRepository.findByMenu(menuId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND))
                    .getOpenTime(LocalDateTime.now().plusDays(1));
        }

        menu.updateVisible(visible, soldOutUntil);
    }

    @Transactional
    @Scheduled(cron = "0 0/10 * * * *")
    public void soldOutCheck() {
        menuRepository.updateAllSoldOutFinishByDateTime(LocalDateTime.now());
    }

    private boolean hasPicture(String picture) {
        return null != picture && !picture.equals("/images/default.jpg");
    }
}
