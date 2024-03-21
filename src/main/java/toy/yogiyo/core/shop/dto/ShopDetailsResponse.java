package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopDetailsResponse {
    private Long id;
    private String name;
    private Long reviewNum;
    private Long likeNum;
    private BigDecimal totalScore;
    private String banner;
    private String noticeTitle;
    private Double distance;
    private Integer minOrderPrice;
    private Integer minDeliveryPrice;
    private Integer deliveryTime;
    private Boolean isLike;
    private Boolean isAvailableDelivery;

    public ShopDetailsResponse(Long id, String name, Long reviewNum, Long likeNum, BigDecimal totalScore, String banner, String noticeTitle, Double distance, Integer minOrderPrice, Integer minDeliveryPrice, Integer deliveryTime, Boolean isLike) {
        this.id = id;
        this.name = name;
        this.reviewNum = reviewNum;
        this.likeNum = likeNum;
        this.totalScore = totalScore;
        this.banner = banner;
        this.noticeTitle = noticeTitle;
        this.distance = distance;
        this.minOrderPrice = minOrderPrice;
        this.minDeliveryPrice = minDeliveryPrice;
        this.deliveryTime = deliveryTime;
        this.isLike = isLike;
    }

    public void setAvailableDelivery(Boolean availableDelivery) {
        isAvailableDelivery = availableDelivery;
    }
}
