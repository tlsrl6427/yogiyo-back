package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.dto.CategoryCreateRequest;
import toy.yogiyo.core.category.dto.CategoryResponse;
import toy.yogiyo.core.category.dto.CategoryUpdateRequest;
import toy.yogiyo.core.category.service.CategoryService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public Long create(@ModelAttribute CategoryCreateRequest request) throws IOException {
        return categoryService.createCategory(request);
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse find(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.findCategory(categoryId);
        return CategoryResponse.from(category);
    }

    @GetMapping("/all")
    public List<CategoryResponse> findAll() {
        return categoryService.getCategories();
    }

    @PatchMapping("/{categoryId}")
    public String update(@PathVariable("categoryId") Long categoryId, CategoryUpdateRequest request) throws IOException {
        categoryService.update(categoryId, request);
        return "success";
    }

    @DeleteMapping("/{categoryId}")
    public String delete(@PathVariable("categoryId") Long categoryId) {
        categoryService.delete(categoryId);
        return "success";
    }
}
