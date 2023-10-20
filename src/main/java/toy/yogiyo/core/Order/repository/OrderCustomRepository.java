package toy.yogiyo.core.Order.repository;

import toy.yogiyo.core.Order.domain.Order;

import java.util.List;

public interface OrderCustomRepository {

    List<Order> scrollOrders(Long memberId, Long lastId);
}
