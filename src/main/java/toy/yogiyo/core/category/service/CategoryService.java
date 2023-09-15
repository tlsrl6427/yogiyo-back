package toy.yogiyo.core.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.dto.CategoryCreateRequest;
import toy.yogiyo.core.category.dto.CategoryResponse;
import toy.yogiyo.core.category.dto.CategoryUpdateRequest;
import toy.yogiyo.core.category.repository.CategoryRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ImageFileHandler imageFileHandler;

    @Transactional
    public Long createCategory(CategoryCreateRequest request) throws IOException {
        validationDuplicatedName(request.getName());
        String storedName = imageFileHandler.store(request.getPicture());
        Category category = request.toEntity(ImageFileUtil.getFilePath(storedName));
        return categoryRepository.save(category).getId();
    }

    @Transactional
    public void update(Long categoryId, CategoryUpdateRequest request) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!request.getPicture().isEmpty()) {
            if (!imageFileHandler.remove(ImageFileUtil.extractFilename(category.getPicture()))) {
                throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
            }

            String storedName = imageFileHandler.store(request.getPicture());
            String imagePath = ImageFileUtil.getFilePath(storedName);

            category.changePicture(imagePath);
        }

        category.changeName(request.getName());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional
    public void delete(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!imageFileHandler.remove(ImageFileUtil.extractFilename(category.getPicture()))) {
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }

        categoryRepository.delete(category);
    }

    private void validationDuplicatedName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new EntityExistsException(ErrorCode.CATEGORY_ALREADY_EXIST);
        }
    }
}
