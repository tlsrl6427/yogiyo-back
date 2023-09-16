package toy.yogiyo.core.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryShopCondition {

    private Double latitude;
    private Double longitude;

    private Long categoryId;

    private String keyword;


    public CategoryShopCondition(Double latitude, Double longitude, Long categoryId, String keyword) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.categoryId = categoryId;
        this.keyword = keyword;
    }
}
