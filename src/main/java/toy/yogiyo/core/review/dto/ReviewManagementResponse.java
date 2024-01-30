package toy.yogiyo.core.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.domain.ReviewImage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewManagementResponse {

    private Long id;

    private BigDecimal tasteScore;
    private BigDecimal quantityScore;
    private BigDecimal deliveryScore;
    private BigDecimal totalScore;
    private String content;
    private String ownerReply;
    private String memberName;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<String> reviewImages = new ArrayList<>();

    @Builder.Default
    private List<MenuDto> menus = new ArrayList<>();

    public void addReviewImage(String image) {
        this.reviewImages.add(image);
    }

    public void addMenu(MenuDto menu) {
        this.menus.add(menu);
    }

    public static ReviewManagementResponse from(Review review) {
        return ReviewManagementResponse.builder()
                .id(review.getId())
                .tasteScore(review.getTasteScore())
                .quantityScore(review.getQuantityScore())
                .deliveryScore(review.getDeliveryScore())
                .totalScore(review.getTotalScore())
                .content(review.getContent())
                .ownerReply(review.getOwnerReply())
                .memberName(review.getMember().getNickname())
                .createdAt(review.getCreatedAt())
                .reviewImages(review.getReviewImages().stream()
                        .map(ReviewImage::getImgSrc)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class MenuDto {
        private String name;
        private int quantity;
    }

}
