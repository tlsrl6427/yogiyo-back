package toy.yogiyo.core.order.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.yogiyo.common.config.TestQuerydslConfiguration;

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