package toy.yogiyo.core.shop.dto;

import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class ShopDetailsResponse {
    private Long id;

    private String name;

    private String ownerNotice;
    private String businessHours;

    private String callNumber;
    private String address;

    private int deliveryTime;
    private List<DeliveryPriceDto> deliveryPrices;

    public static ShopDetailsResponse from(Shop shop) {
        ShopDetailsResponse response = new ShopDetailsResponse();

        response.setId(shop.getId());
        response.setName(shop.getName());
        response.setOwnerNotice(shop.getOwnerNotice());
        response.setBusinessHours(shop.getBusinessHours());
        response.setCallNumber(shop.getCallNumber());
        response.setAddress(shop.getAddress());
        response.setDeliveryTime(shop.getDeliveryTime());

        response.setDeliveryPrices(shop.getDeliveryPriceInfos().stream()
                .map(d -> new DeliveryPriceDto(d.getOrderPrice(), d.getDeliveryPrice()))
                .collect(Collectors.toList()));

        return response;
    }
}
