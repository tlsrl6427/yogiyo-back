package toy.yogiyo.core.shop.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.core.shop.domain.DeliveryPrice;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class ShopRegisterRequest {

    private String name;


    private MultipartFile icon;
    private MultipartFile banner;

    private String ownerNotice;
    private String businessHours;

    private String callNumber;
    private String address;

    private int deliveryTime;
    private String orderTypes;
    private int packagingPrice;
    private List<DeliveryPriceDto> deliveryPriceDtos;

    public Shop toEntity(String iconStoredName, String bannerStoredName) {
        Shop shop = new Shop(name, iconStoredName, bannerStoredName, ownerNotice, businessHours, callNumber,
                address, deliveryTime, orderTypes, packagingPrice);

        List<DeliveryPrice> deliveryPrices = deliveryPriceDtos.stream()
                .map(DeliveryPriceDto::toEntity)
                .collect(Collectors.toList());

        shop.changeDeliveryPrices(deliveryPrices);

        return shop;
    }
}
