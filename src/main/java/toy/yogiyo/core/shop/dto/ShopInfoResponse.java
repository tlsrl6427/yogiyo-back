package toy.yogiyo.core.shop.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ShopInfoResponse {

    private Long id;
    private String name;
    private String callNumber;
    private String address;
    private List<String> categories;

    public static ShopInfoResponse from(Shop shop) {
        return ShopInfoResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .callNumber(shop.getCallNumber())
                .address(shop.getAddress())
                .categories(shop.getCategoryShop().stream()
                        .map(categoryShop -> categoryShop.getCategory().getName())
                        .collect(Collectors.toList()))
                .build();
    }


}
