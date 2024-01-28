package toy.yogiyo.core.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.repository.MenuRepository;
import toy.yogiyo.core.order.domain.Order;
import toy.yogiyo.core.order.domain.OrderItem;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public void create(Member member, ReviewCreateRequest request){
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
        Review review = request.toReview(member, order);
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));
        shop.addReview(review);

        Long[] menuIds = order.getOrderItems().stream()
                .map(OrderItem::getMenuId)
                .toArray(Long[]::new);
        List<Menu> menus = menuRepository.findMenus(menuIds);
        for (Menu menu : menus) {
            menu.increaseReviewNum();
        }

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
