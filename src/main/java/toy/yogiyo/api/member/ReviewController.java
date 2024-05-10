package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.review.dto.*;
import toy.yogiyo.core.review.repository.ReviewQueryRepository;
import toy.yogiyo.core.review.service.ReviewService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewQueryRepository reviewQueryRepository;

    @PostMapping("/write")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@LoginUser Member member, @Valid @RequestBody ReviewCreateRequest request){
        reviewService.create(member, request);
    }

    @GetMapping("/memberReview")
    @ResponseStatus(HttpStatus.OK)
    public MemberReviewScrollResponse getMemberReview(@LoginUser Member member, @RequestParam Long lastId){
        return reviewService.getMemberReviews(member, lastId);
    }


    @GetMapping("/shop-review")
    @ResponseStatus(HttpStatus.OK)
    public Scroll<ReviewResponse> getShopReview(@RequestParam Long shopId, @Validated @ModelAttribute ReviewQueryCondition condition){
        return reviewQueryRepository.shopReviewScroll(shopId, condition);
    }

    @GetMapping("/shop-review-summary")
    @ResponseStatus(HttpStatus.OK)
    public ReviewGetSummaryResponse getShopReviewSummary(@RequestParam Long shopId){
        return reviewQueryRepository.findReviewSummary(shopId);
    }
}
