package toy.yogiyo.core.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.order.domain.Order;
import toy.yogiyo.core.order.repository.OrderRepository;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.dto.MemberReviewScrollResponse;
import toy.yogiyo.core.review.dto.ReviewCreateRequest;
import toy.yogiyo.core.review.dto.ShopReviewScrollRequest;
import toy.yogiyo.core.review.dto.ShopReviewScrollResponse;
import toy.yogiyo.core.review.repository.ReviewRepository;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;

    public void create(Member member, ReviewCreateRequest request){
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
        Review review = request.toReview(member, order);
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));
        shop.addReview(review);
        reviewRepository.save(review);
    }

    public MemberReviewScrollResponse getMemberReviews(Member member, Long lastId){
        List<Review> reviews = reviewRepository.scrollByLastId(lastId);
        boolean hasNext = reviews.size() >= 6;
        if(hasNext) reviews.remove(reviews.size()-1);
        Long nextLastId = reviews.get(reviews.size()-1).getId();

        return MemberReviewScrollResponse.builder()
                .reviews(reviews)
                .nextLastId(nextLastId)
                .hasNext(hasNext)
                .build();
    }
}
