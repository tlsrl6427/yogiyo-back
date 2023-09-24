package toy.yogiyo.core.food.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.food.domain.Food;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@Builder
public class FoodAddRequest {

    private String name;
    private String content;
    private int price;
    private Long shopId;

    public Food toEntity() {
        return Food.builder()
                .name(name)
                .content(content)
                .price(price)
                .shop(Shop.builder().id(shopId).build())
                .build();
    }
}
