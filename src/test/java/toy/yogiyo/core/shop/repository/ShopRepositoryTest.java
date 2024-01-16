package toy.yogiyo.core.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.config.TestQuerydslConfiguration;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
@Import(TestQuerydslConfiguration.class)
class ShopRepositoryTest {

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("존재하는 가게명인지 확인")
    void existsByName() throws Exception {
        // given
        Shop shop = getShop();
        shopRepository.save(shop);

        // when
        boolean isExists = shopRepository.existsByName(shop.getName());

        // then
        assertThat(isExists).isEqualTo(true);
    }

    private Shop getShop() {
        return Shop.builder()
                .name("롯데리아")
                .icon("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .banner("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .ownerNotice("사장님 공지")
                .callNumber("010-1234-5678")
                .address("서울 강남구 영동대로 513")
                .build();
    }

}