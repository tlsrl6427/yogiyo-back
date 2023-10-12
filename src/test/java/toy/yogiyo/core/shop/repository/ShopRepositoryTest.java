package toy.yogiyo.core.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
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
            shop.changeDeliveryPrices(Arrays.asList(new DeliveryPriceInfo(), new DeliveryPriceInfo(), new DeliveryPriceInfo()));

            // when
            shopRepository.save(shop);

            // then
            for (DeliveryPriceInfo deliveryPriceInfo : shop.getDeliveryPriceInfos()) {
                DeliveryPriceInfo findDeliveryPriceInfo = em.find(DeliveryPriceInfo.class, deliveryPriceInfo.getId());
                assertThat(findDeliveryPriceInfo).isEqualTo(deliveryPriceInfo);
            }
        }

        @Test
        @DisplayName("shop 삭제시 deliveryPrice도 같이 삭제")
        void deleteShopDeliveryPrice() throws Exception {
            // given
            Shop shop = getShop();
            shop.changeDeliveryPrices(Arrays.asList(new DeliveryPriceInfo(), new DeliveryPriceInfo(), new DeliveryPriceInfo()));

            shopRepository.save(shop);

            // when
            shopRepository.delete(shop);

            // then
            for (DeliveryPriceInfo deliveryPriceInfo : shop.getDeliveryPriceInfos()) {
                DeliveryPriceInfo findDeliveryPriceInfo = em.find(DeliveryPriceInfo.class, deliveryPriceInfo.getId());
                assertThat(findDeliveryPriceInfo).isNull();
            }
        }

        @Test
        @DisplayName("shop의 deliveryPrices 아이템 변경하면 기존에 있던 deliveryPrices 삭제")
        void shopChangeDeliveryPrices() throws Exception {
            // given
            Shop shop = getShop();
            List<DeliveryPriceInfo> deliveryPriceInfos = Arrays.asList(new DeliveryPriceInfo(), new DeliveryPriceInfo(), new DeliveryPriceInfo());
            List<DeliveryPriceInfo> newDeliveryPriceInfos = Arrays.asList(new DeliveryPriceInfo(), new DeliveryPriceInfo(), new DeliveryPriceInfo());

            shop.changeDeliveryPrices(deliveryPriceInfos);
            shopRepository.save(shop);
            em.flush();

            // when
            shop.changeDeliveryPrices(newDeliveryPriceInfos);
            em.flush();

            // then
            for (DeliveryPriceInfo deliveryPriceInfo : deliveryPriceInfos) {
                assertThat(em.find(DeliveryPriceInfo.class, deliveryPriceInfo.getId())).isNull();
            }
            for (DeliveryPriceInfo newDeliveryPriceInfo : newDeliveryPriceInfos) {
                assertThat(em.find(DeliveryPriceInfo.class, newDeliveryPriceInfo.getId())).isNotNull();
            }
        }
    }

    private Shop getShop() {
        return Shop.builder()
                .name("롯데리아")
                .icon("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .banner("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .ownerNotice("사장님 공지")
                .callNumber("010-1234-5678")
                .address("서울 강남구 영동대로 513")
                .deliveryTime(30)
                .build();
    }

}