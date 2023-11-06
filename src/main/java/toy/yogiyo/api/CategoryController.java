package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.dto.*;
import toy.yogiyo.core.category.service.CategoryService;
import toy.yogiyo.core.category.service.CategoryShopService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryShopService categoryShopService;

    @PostMapping("/create")
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
    public String update(@PathVariable("categoryId") Long categoryId, CategoryUpdateRequest request) throws IOException {
        categoryService.update(categoryId, request);
        return "success";
    }

    @DeleteMapping("/{categoryId}")
    public String delete(@PathVariable("categoryId") Long categoryId) {
        categoryService.delete(categoryId);
        return "success";
    }

    @GetMapping("/{categoryId}/shop")
    public Slice<CategoryShopResponse> getAroundShop(@PathVariable("categoryId") Long categoryId,
                                                     @ModelAttribute CategoryShopCondition condition,
                                                     Pageable pageable) {

        return categoryShopService.findShop(categoryId, condition, pageable);
    }
}
