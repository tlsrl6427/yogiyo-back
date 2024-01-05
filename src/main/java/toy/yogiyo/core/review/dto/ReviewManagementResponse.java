package toy.yogiyo.core.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.domain.ReviewImage;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private LocalDate date;

    @Builder.Default
    private List<String> reviewImages = new ArrayList<>();

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
                .date(review.getCreatedAt().toLocalDate())
                .reviewImages(review.getReviewImages().stream()
                        .map(ReviewImage::getImgSrc)
                        .collect(Collectors.toList()))
                .build();
    }

}
