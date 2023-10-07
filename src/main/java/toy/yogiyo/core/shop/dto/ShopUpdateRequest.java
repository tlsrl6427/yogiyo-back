package toy.yogiyo.core.shop.dto;


import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.category.dto.CategoryDto;

import java.util.List;

@Getter @Setter
public class ShopUpdateRequest {

    private String name;

    private String ownerNotice;
    private String businessHours;

    private String callNumber;
    private String address;

    private int deliveryTime;
    private List<DeliveryPriceDto> deliveryPrices;
    private List<CategoryDto> categories;

}
