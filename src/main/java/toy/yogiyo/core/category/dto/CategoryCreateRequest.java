package toy.yogiyo.core.category.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.core.category.domain.Category;

@Getter @Setter
public class CategoryCreateRequest {

    private String name;
    private MultipartFile picture;

    public Category toEntity(String picture) {
        return new Category(name, picture);
    }
}
