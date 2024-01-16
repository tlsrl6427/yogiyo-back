package toy.yogiyo.core.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.order.domain.Order;
import toy.yogiyo.core.order.dto.*;
import toy.yogiyo.core.order.repository.OrderRepository;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.repository.ShopRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;

    public void createOrder(Member member, OrderCreateRequest orderCreateRequest){
        Shop findShop = shopRepository.findById(orderCreateRequest.getShopId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));
        Order order = Order.createOrder(member, findShop, orderCreateRequest);

        findShop.increaseOrderNum();
        orderRepository.save(order);
    }

    public OrderHistoryResponse getOrderHistory(Member member, Long lastId){
        List<Order> orders = orderRepository.scrollOrders(member.getId(), lastId);
        boolean hasNext = orders.size() >= 6;
        if(hasNext) orders.remove(orders.size()-1);
        List<OrderHistory> orderHistoryList = orders.stream()
                .map(OrderHistory::from)
                .collect(Collectors.toList());
        Long nextLastId = orders.get(orders.size() - 1).getId();

        return OrderHistoryResponse.builder()
                .orderHistories(orderHistoryList)
                .lastId(nextLastId)
                .hasNext(hasNext)
                .build();
    }

    public OrderDetailResponse getOrderDetail(Member member, Long orderId){
        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));

        validateMember(member, findOrder);
        return OrderDetailResponse.from(findOrder);
    }

    private void validateMember(Member member, Order order) {
        if(member.getId() != order.getMember().getId()) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
    }

}
