package toy.yogiyo.core.category.dto;

import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.category.domain.Category;

@Getter @Setter
public class CategoryDto {

    private Long id;
    private String name;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category toCategory() {
        return new Category(id, name);
    }
}
