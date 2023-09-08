package toy.yogiyo.core.shop.dto;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ShopUpdateRequest {

    private Long id;

    private String name;

    private String ownerNotice;
    private String businessHours;

    private String callNumber;
    private String address;

    private int deliveryTime;
    private int leastOrderPrice;
    private String orderTypes;
    private int deliveryPrice;
    private int packagingPrice;

}
