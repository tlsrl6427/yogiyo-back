package toy.yogiyo.api.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.dto.*;
import toy.yogiyo.core.category.service.CategoryService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@ModelAttribute CategoryCreateRequest request) throws IOException {
        return categoryService.createCategory(request);
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse get(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getCategory(categoryId);
        return CategoryResponse.from(category);
    }

    @GetMapping("/all")
    public List<CategoryResponse> getAll() {
        return categoryService.getCategories();
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("categoryId") Long categoryId, CategoryUpdateRequest request) throws IOException {
        categoryService.update(categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("categoryId") Long categoryId) {
        categoryService.delete(categoryId);
    }

}
