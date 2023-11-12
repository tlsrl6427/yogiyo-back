package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.review.dto.MemberReviewScrollResponse;
import toy.yogiyo.core.review.dto.ReviewCreateRequest;
import toy.yogiyo.core.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/write")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@LoginUser Member member, @RequestBody ReviewCreateRequest request){
        reviewService.create(member, request);
    }

    @GetMapping("/memberReview")
    @ResponseStatus(HttpStatus.OK)
    public MemberReviewScrollResponse getMemberReview(@LoginUser Member member, @RequestParam Long lastId){
        return reviewService.getMemberReviews(member, lastId);
    }
}
