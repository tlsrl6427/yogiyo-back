package toy.yogiyo.core.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.core.shop.domain.DeliveryPrice;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
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

    @Nested
    @DisplayName("영속성 전이")
    class Cascade {
        @Test
        @DisplayName("shop 저장시 deliveryPrice도 같이 저장")
        void saveShopDeliveryPrice() throws Exception {
            // given
            Shop shop = getShop();
            shop.changeDeliveryPrices(Arrays.asList(new DeliveryPrice(), new DeliveryPrice(), new DeliveryPrice()));

            // when
            shopRepository.save(shop);

            // then
            for (DeliveryPrice deliveryPrice : shop.getDeliveryPrices()) {
                DeliveryPrice findDeliveryPrice = em.find(DeliveryPrice.class, deliveryPrice.getId());
                assertThat(findDeliveryPrice).isEqualTo(deliveryPrice);
            }
        }

        @Test
        @DisplayName("shop 삭제시 deliveryPrice도 같이 삭제")
        void deleteShopDeliveryPrice() throws Exception {
            // given
            Shop shop = getShop();
            shop.changeDeliveryPrices(Arrays.asList(new DeliveryPrice(), new DeliveryPrice(), new DeliveryPrice()));

            shopRepository.save(shop);

            // when
            shopRepository.delete(shop);

            // then
            for (DeliveryPrice deliveryPrice : shop.getDeliveryPrices()) {
                DeliveryPrice findDeliveryPrice = em.find(DeliveryPrice.class, deliveryPrice.getId());
                assertThat(findDeliveryPrice).isNull();
            }
        }

        @Test
        @DisplayName("shop의 deliveryPrices 아이템 변경하면 기존에 있던 deliveryPrices 삭제")
        void shopChangeDeliveryPrices() throws Exception {
            // given
            Shop shop = getShop();
            List<DeliveryPrice> deliveryPrices = Arrays.asList(new DeliveryPrice(), new DeliveryPrice(), new DeliveryPrice());
            List<DeliveryPrice> newDeliveryPrices = Arrays.asList(new DeliveryPrice(), new DeliveryPrice(), new DeliveryPrice());

            shop.changeDeliveryPrices(deliveryPrices);
            shopRepository.save(shop);
            em.flush();

            // when
            shop.changeDeliveryPrices(newDeliveryPrices);
            em.flush();

            // then
            for (DeliveryPrice deliveryPrice : deliveryPrices) {
                assertThat(em.find(DeliveryPrice.class, deliveryPrice.getId())).isNull();
            }
            for (DeliveryPrice newDeliveryPrice : newDeliveryPrices) {
                assertThat(em.find(DeliveryPrice.class, newDeliveryPrice.getId())).isNotNull();
            }
        }
    }

    private Shop getShop() {
        return new Shop("롯데리아",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);
    }

}