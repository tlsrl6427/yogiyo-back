package toy.yogiyo.core.shop.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ShopUpdateRequest {

    private Long id;

    private String name;

    private String ownerNotice;
    private String businessHours;

    private String callNumber;
    private String address;

    private int deliveryTime;
    private String orderTypes;
    private int packagingPrice;
    private List<DeliveryPriceDto> deliveryPriceDtos;

}
