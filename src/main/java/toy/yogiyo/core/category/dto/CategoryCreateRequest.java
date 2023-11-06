package toy.yogiyo.core.category.dto;

import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.category.domain.Category;

@Getter @Setter
public class CategoryCreateRequest {

    private String name;

    public Category toCategory() {
        return Category.builder()
                .name(name)
                .build();
    }
}
