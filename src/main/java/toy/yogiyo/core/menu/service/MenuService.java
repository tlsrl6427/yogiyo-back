package toy.yogiyo.core.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.repository.MenuRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ImageFileHandler imageFileHandler;

    @Transactional
    public Long add(Menu menu) {
        menuRepository.save(menu);
        return menu.getId();
    }

    @Transactional
    public void changePicture(Long menuId, MultipartFile picture) throws IOException {
        Menu menu = find(menuId);

        if(null != menu.getPicture() && !imageFileHandler.remove(ImageFileUtil.extractFilename(menu.getPicture()))){
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }

        menu.changePicture(ImageFileUtil.getFilePath(imageFileHandler.store(picture)));
    }

    @Transactional(readOnly = true)
    public Menu find(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));
    }

    @Transactional
    public void update(Menu updateParam) {
        Menu menu = find(updateParam.getId());
        menu.changeInfo(updateParam);
    }

    @Transactional
    public void delete(Menu param) {
        Menu menu = find(param.getId());

        if(!imageFileHandler.remove(ImageFileUtil.extractFilename(menu.getPicture()))){
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }

        menuRepository.delete(menu);
    }

}
