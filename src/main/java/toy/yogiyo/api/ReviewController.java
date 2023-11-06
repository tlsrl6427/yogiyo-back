package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.review.dto.MemberReviewScrollResponse;
import toy.yogiyo.core.review.dto.ReviewWriteRequest;
import toy.yogiyo.core.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/write")
    public void write(@LoginUser Member member, @RequestBody ReviewWriteRequest request){
        reviewService.write(member, request);
    }

    @GetMapping("/memberReview")
    public MemberReviewScrollResponse getMemberReview(@LoginUser Member member, @RequestParam Long lastId){
        return reviewService.getMemberReviews(member, lastId);
    }

    @GetMapping("/shopReview")
    public void getShopReview(){

    }
}
