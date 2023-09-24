package toy.yogiyo.core.food.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.food.domain.Food;
import toy.yogiyo.core.food.repository.FoodRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final ImageFileHandler imageFileHandler;

    @Transactional
    public Long add(Food food, MultipartFile picture) throws IOException {
        food.changePicture(ImageFileUtil.getFilePath(imageFileHandler.store(picture)));
        foodRepository.save(food);

        return food.getId();
    }

    @Transactional(readOnly = true)
    public Food find(Long foodId) {
        return foodRepository.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FOOD_NOT_FOUND));
    }

    @Transactional
    public void update(Food updateParam) {
        Food food = find(updateParam.getId());
        food.changeInfo(updateParam);
    }

    @Transactional
    public void delete(Food param) {
        Food food = find(param.getId());

        if(!imageFileHandler.remove(ImageFileUtil.extractFilename(food.getPicture()))){
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }

        foodRepository.delete(food);
    }

}
