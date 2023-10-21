package toy.yogiyo.core.Order.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.yogiyo.common.config.TestQuerydslConfiguration;
import toy.yogiyo.core.Order.domain.Order;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestQuerydslConfiguration.class)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void beforeEach(){

    }

    @DisplayName("오더 무한 스크롤")
    @Test
    void scrollOrders(){

    }
}