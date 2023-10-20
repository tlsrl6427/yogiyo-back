package toy.yogiyo.core.shop.dto;


import lombok.*;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegisterRequest {

    private String name;
    private String callNumber;
    private String address;
    private Double longitude;
    private Double latitude;
    @Builder.Default
    private List<String> categories = new ArrayList<>();

    public Shop toEntity(String iconStoredName, String bannerStoredName, Owner owner) {
        return Shop.builder()
                .name(name)
                .icon(iconStoredName)
                .banner(bannerStoredName)
                .callNumber(callNumber)
                .address(address)
                .longitude(longitude)
                .latitude(latitude)
                .owner(owner)
                .build();
    }
}
