package toy.yogiyo.core.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor
public class ReviewGetSummaryResponse {
    private long totalCount;
    private long totalOwnerReplyCount;
    private long totalNoReplyCount;
    private BigDecimal avgTotalScore;
    private BigDecimal avgTasteScore;
    private BigDecimal avgQuantityScore;
    private BigDecimal avgDeliveryScore;

    @Builder
    public ReviewGetSummaryResponse(long totalCount, long totalOwnerReplyCount, double avgTotalScore, double avgTasteScore, double avgQuantityScore, double avgDeliveryScore) {
        this.totalCount = totalCount;
        this.totalOwnerReplyCount = totalOwnerReplyCount;
        this.totalNoReplyCount = totalCount - totalOwnerReplyCount;
        this.avgTotalScore = BigDecimal.valueOf(avgTotalScore).setScale(1, RoundingMode.HALF_UP);
        this.avgTasteScore = BigDecimal.valueOf(avgTasteScore).setScale(1, RoundingMode.HALF_UP);
        this.avgQuantityScore = BigDecimal.valueOf(avgQuantityScore).setScale(1, RoundingMode.HALF_UP);
        this.avgDeliveryScore = BigDecimal.valueOf(avgDeliveryScore).setScale(1, RoundingMode.HALF_UP);
    }
}
