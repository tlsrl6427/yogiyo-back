package toy.yogiyo.core.Order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import toy.yogiyo.core.Order.domain.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {

}
