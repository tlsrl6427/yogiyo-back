package toy.yogiyo.core.category.dto;

import lombok.Getter;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
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
    private List<Integer> deliveryPriceInfos;

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
        this.deliveryPriceInfos = shop.getDeliveryPriceInfos().stream()
                .map(DeliveryPriceInfo::getDeliveryPrice)
                .collect(Collectors.toList());
    }

}
