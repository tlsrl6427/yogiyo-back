package toy.yogiyo.core.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.shop.domain.Shop;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {

    private Long likeId;
    private Long shopId;
    private String shopName;
    private String shopImg;
    private BigDecimal score;
}
