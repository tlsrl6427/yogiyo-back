package toy.yogiyo.core.menu.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.yogiyo.common.config.TestQuerydslConfiguration;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Import(TestQuerydslConfiguration.class)
class SignatureMenuRepositoryTest {

    @Autowired
    SignatureMenuRepository signatureMenuRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("대표 메뉴 정렬 마지막 순서 조회")
    void findMaxOrder() throws Exception {
        // given
        Shop shop = Shop.builder()
                .name("네네치킨")
                .icon("icon.png")
                .banner("banner.png")
                .ownerNotice("사장님 공지")
                .callNumber("010-1234-1234")
                .address("주소")
                .build();
        em.persist(shop);

        for (int i = 0; i < 5; i++) {
            Menu menu = Menu.builder()
                    .name("메뉴 " + i)
                    .content("메뉴 " + i + " 설명")
                    .price(10000)
                    .build();
            em.persist(menu);

            SignatureMenu signatureMenu = SignatureMenu.builder()
                    .shop(shop)
                    .menu(menu)
                    .position(i + 1)
                    .build();
            em.persist(signatureMenu);
        }

        // when
        Integer maxOrder = signatureMenuRepository.findMaxOrder(shop.getId());

        // then
        assertThat(maxOrder).isEqualTo(5);
    }

    @Test
    @DisplayName("대표 메뉴 전체 조회")
    void findAllByShopId() throws Exception {
        // given
        Shop shop = Shop.builder()
                .name("네네치킨")
                .icon("icon.png")
                .banner("banner.png")
                .ownerNotice("사장님 공지")
                .callNumber("010-1234-1234")
                .address("주소")
                .build();
        em.persist(shop);

        for (int i = 0; i < 5; i++) {
            Menu menu = Menu.builder()
                    .name("메뉴 " + i)
                    .content("메뉴 " + i + " 설명")
                    .price(10000)
                    .build();
            em.persist(menu);

            SignatureMenu signatureMenu = SignatureMenu.builder()
                    .shop(shop)
                    .menu(menu)
                    .position(i + 1)
                    .build();
            em.persist(signatureMenu);
        }

        // when
        List<SignatureMenu> findSignatureMenus = signatureMenuRepository.findAlLByShopId(shop.getId());

        // then
        assertThat(findSignatureMenus.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("대표 메뉴 전체 삭제")
    void deleteAllByShopId() throws Exception {
        // given
        Shop shop = Shop.builder()
                .name("네네치킨")
                .icon("icon.png")
                .banner("banner.png")
                .ownerNotice("사장님 공지")
                .callNumber("010-1234-1234")
                .address("주소")
                .build();
        em.persist(shop);

        for (int i = 0; i < 5; i++) {
            Menu menu = Menu.builder()
                    .name("메뉴 " + i)
                    .content("메뉴 " + i + " 설명")
                    .price(10000)
                    .build();
            em.persist(menu);

            SignatureMenu signatureMenu = SignatureMenu.builder()
                    .shop(shop)
                    .menu(menu)
                    .position(i + 1)
                    .build();
            em.persist(signatureMenu);
        }

        // when
        int deleteCount = signatureMenuRepository.deleteAllByShopId(shop.getId());

        // then
        assertThat(deleteCount).isEqualTo(5);
    }

}