package toy.yogiyo.core.category.dto;

import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.category.domain.Category;

@Getter @Setter
public class CategoryResponse {

    private Long id;
    private String name;
    private String picture;

    public static CategoryResponse from(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setPicture(category.getPicture());

        return response;
    }
}
