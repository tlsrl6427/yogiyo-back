package toy.yogiyo.core.category.dto;

import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.category.domain.Category;

@Getter @Setter
public class CategoryDto {

    private Long id;
    private String name;
    private String picture;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public Category toEntity() {
        return new Category(id, name, picture);
    }
}
