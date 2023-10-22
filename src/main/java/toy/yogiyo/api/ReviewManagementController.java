package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.core.Review.domain.Review;
import toy.yogiyo.core.Review.dto.ReviewManagementResponse;
import toy.yogiyo.core.Review.dto.ReviewQueryCondition;
import toy.yogiyo.core.Review.repository.ReviewQueryRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management/review")
public class ReviewManagementController {

    private final ReviewQueryRepository reviewQueryRepository;

    @GetMapping("/shop/{shopId}")
    public Scroll<ReviewManagementResponse> getShopReviews(@PathVariable Long shopId,
                                                           @ModelAttribute ReviewQueryCondition condition) {

        Scroll<Review> reviews = reviewQueryRepository.shopReviewScroll(shopId, condition);
        return reviews.map(ReviewManagementResponse::from);
    }

}
