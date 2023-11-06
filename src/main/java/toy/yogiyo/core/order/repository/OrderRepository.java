package toy.yogiyo.core.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {

}
