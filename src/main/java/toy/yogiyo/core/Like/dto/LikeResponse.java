package toy.yogiyo.core.Like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.shop.domain.Shop;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {

    private Long shopId;
    private String shopName;
    private String shopImg;
    private String score;

    public static LikeResponse from(Shop shop){
        return LikeResponse.builder()
                .shopId(shop.getId())
                .shopName(shop.getName())
                .shopImg(shop.getIcon())
                .score(String.format("%.1f", shop.getTasteScore()
                        +shop.getDeliveryScore()
                        +shop.getQuantityScore()))
                .build();
    }
}
