package toy.yogiyo.core.category.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.core.category.domain.Category;

@Getter @Setter
public class CategoryUpdateRequest {

    private String name;

    public Category toEntity() {
        return Category.builder()
                .name(name)
                .build();
    }
}
