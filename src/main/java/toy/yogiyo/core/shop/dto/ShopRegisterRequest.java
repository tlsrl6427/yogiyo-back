package toy.yogiyo.core.shop.dto;


import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

@Getter @Setter
public class ShopRegisterRequest {

    private String name;
    private String callNumber;
    private String address;
    private List<Long> categoryIds;

    public Shop toEntity(String iconStoredName, String bannerStoredName) {
        return Shop.builder()
                .name(name)
                .icon(iconStoredName)
                .banner(bannerStoredName)
                .callNumber(callNumber)
                .address(address)
                .build();
    }
}
