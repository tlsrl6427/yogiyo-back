package toy.yogiyo.core.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryShopCondition {

    private Double latitude;
    private Double longitude;

    private String keyword;

    public CategoryShopCondition() {
    }

    public CategoryShopCondition(Double latitude, Double longitude, String keyword) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.keyword = keyword;
    }
}
