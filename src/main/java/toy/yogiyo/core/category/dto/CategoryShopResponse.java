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

    public static CategoryShopResponse from(CategoryShop categoryShop, int distance) {
        Shop shop = categoryShop.getShop();

        CategoryShopResponse categoryShopResponse = new CategoryShopResponse();
        categoryShopResponse.name = shop.getName();
        categoryShopResponse.icon = shop.getIcon();
        categoryShopResponse.distance = distance;
        categoryShopResponse.stars = (shop.getDeliveryScore() +
                shop.getTasteScore() +
                shop.getQuantityScore()) / 3;
        categoryShopResponse.reviewNum = shop.getReviewNum();
        categoryShopResponse.deliveryTime = shop.getDeliveryTime();
        categoryShopResponse.deliveryPriceInfos = shop.getDeliveryPriceInfos().stream()
                .map(DeliveryPriceInfo::getDeliveryPrice)
                .collect(Collectors.toList());

        return categoryShopResponse;
    }

}
