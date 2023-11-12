package toy.yogiyo.core.shop.dto;


import lombok.*;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.Shop;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegisterRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String callNumber;
    @NotBlank
    private String address;
    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
    @NotNull
    @Builder.Default
    private List<String> categories = new ArrayList<>();

    public Shop toShop(String iconStoredName, String bannerStoredName, Owner owner) {
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
