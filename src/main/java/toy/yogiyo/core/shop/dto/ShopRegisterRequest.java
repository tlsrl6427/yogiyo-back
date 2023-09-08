package toy.yogiyo.core.shop.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.core.shop.Shop;

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
    private int leastOrderPrice;
    private String orderTypes;
    private int deliveryPrice;
    private int packagingPrice;

    public Shop toEntity(String iconStoredName, String bannerStoredName) {
        return new Shop(name, iconStoredName, bannerStoredName, ownerNotice, businessHours, callNumber,
                address, deliveryTime, leastOrderPrice, orderTypes, deliveryPrice, packagingPrice);
    }
}
