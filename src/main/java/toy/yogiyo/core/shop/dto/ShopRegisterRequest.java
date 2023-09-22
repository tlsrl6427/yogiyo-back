package toy.yogiyo.core.shop.dto;


import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.category.dto.CategoryDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class ShopRegisterRequest {

    private String name;

    private String ownerNotice;
    private String businessHours;

    private String callNumber;
    private String address;

    private int deliveryTime;
    private String orderTypes;
    private int packagingPrice;
    private List<DeliveryPriceDto> deliveryPrices;
    private List<CategoryDto> categories;

    public Shop toEntity(String iconStoredName, String bannerStoredName) {
        Shop shop = new Shop(name, iconStoredName, bannerStoredName, ownerNotice, businessHours, callNumber,
                address, deliveryTime, orderTypes, packagingPrice);

        List<DeliveryPriceInfo> deliveryPriceInfos = this.deliveryPrices.stream()
                .map(DeliveryPriceDto::toEntity)
                .collect(Collectors.toList());

        shop.changeDeliveryPrices(deliveryPriceInfos);

        return shop;
    }
}
