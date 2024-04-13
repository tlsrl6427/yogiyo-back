package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.order.dto.OrderCreateRequest;
import toy.yogiyo.core.order.dto.OrderCreateResponse;
import toy.yogiyo.core.order.dto.OrderDetailResponse;
import toy.yogiyo.core.order.dto.OrderHistoryResponse;
import toy.yogiyo.core.order.service.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreateResponse createOrder(@LoginUser Member member, @Valid @RequestBody OrderCreateRequest orderCreateRequest){
        return orderService.createOrder(member, orderCreateRequest);
    }

    @GetMapping("/scroll")
    @ResponseStatus(HttpStatus.OK)
    public OrderHistoryResponse scrollOrderHistories(@LoginUser Member member, @RequestParam(required = false) Long lastId){
        return orderService.getOrderHistory(member, lastId);
    }

    @GetMapping("/details")
    @ResponseStatus(HttpStatus.OK)
    public OrderDetailResponse getOrderDetails(@LoginUser Member member, @RequestParam Long orderId){
        return orderService.getOrderDetail(member, orderId);
    }

    @GetMapping("/writableReview")
    @ResponseStatus(HttpStatus.OK)
    public OrderHistoryResponse getWritableReviews(@LoginUser Member member, @RequestParam(required = false) Long lastId){
        return orderService.getWritableReview(member, lastId);
    }
}
