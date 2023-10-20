package toy.yogiyo.core.Review.dto;

import lombok.*;
import toy.yogiyo.core.Review.domain.Review;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberReviewScrollResponse {

    private List<Review> reviews;
    private Long nextLastId;
    private boolean hasNext;
}
