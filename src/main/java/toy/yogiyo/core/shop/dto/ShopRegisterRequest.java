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
    private List<DeliveryPriceDto> deliveryPrices;
    private List<CategoryDto> categories;

    public Shop toEntity(String iconStoredName, String bannerStoredName) {
        Shop shop = Shop.builder()
                .name(name)
                .icon(iconStoredName)
                .banner(bannerStoredName)
                .ownerNotice(ownerNotice)
                .businessHours(businessHours)
                .callNumber(callNumber)
                .address(address)
                .deliveryTime(deliveryTime)
                .build();

        List<DeliveryPriceInfo> deliveryPriceInfos = this.deliveryPrices.stream()
                .map(DeliveryPriceDto::toEntity)
                .collect(Collectors.toList());

        shop.changeDeliveryPrices(deliveryPriceInfos);

        return shop;
    }
}
