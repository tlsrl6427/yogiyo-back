package toy.yogiyo.core.order.repository;

import toy.yogiyo.core.order.domain.Order;

import java.util.List;

public interface OrderCustomRepository {

    List<Order> scrollOrders(Long memberId, Long lastId);
    List<Order> scrollWritableReviews(Long memberId, Long lastId);
}
