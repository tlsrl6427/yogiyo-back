package toy.yogiyo.core.category.dto;

import lombok.Getter;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.shop.domain.DeliveryPrice;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CategoryShopResponse {

    private String name;
    private String icon;
    private int distance;
    private double stars;
    private long reviewNum;
    private int deliveryTime;
    private List<Integer> deliveryPrices;

    public CategoryShopResponse(CategoryShop categoryShop, int distance) {
        Shop shop = categoryShop.getShop();

        this.name = shop.getName();
        this.icon = shop.getIcon();
        this.distance = distance;
        this.stars = (shop.getDeliveryScore() +
                shop.getTasteScore() +
                shop.getQuantityScore()) / 3;
        this.reviewNum = shop.getReviewNum();
        this.deliveryTime = shop.getDeliveryTime();
        this.deliveryPrices = shop.getDeliveryPrices().stream()
                .map(DeliveryPrice::getDeliveryPrice)
                .collect(Collectors.toList());
    }

}
