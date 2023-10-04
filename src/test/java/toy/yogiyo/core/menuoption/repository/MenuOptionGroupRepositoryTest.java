package toy.yogiyo.core.menuoption.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MenuOptionGroupRepositoryTest {

    @Autowired
    MenuOptionGroupRepository menuOptionGroupRepository;

    @Autowired
    EntityManager em;

    @Autowired
    EntityManagerFactory emf;

    @Test
    @DisplayName("옵션 그룹 정렬 마지막 순서 조회")
    void findMaxOrder() throws Exception {
        // given
        Shop shop = Shop.builder()
                .name("네네치킨")
                .icon("icon.png")
                .banner("banner.png")
                .ownerNotice("사장님 공지")
                .businessHours("매일")
                .callNumber("010-1234-1234")
                .address("주소")
                .orderTypes("배달")
                .build();
        em.persist(shop);

        for (int i = 0; i < 5; i++) {
            em.persist(MenuOptionGroup.builder()
                    .shop(shop)
                    .position(i + 1)
                    .build());
        }

        // when
        Integer maxOrder = menuOptionGroupRepository.findMaxOrder(shop.getId());

        // then
        assertThat(maxOrder).isEqualTo(5);
    }

    @Test
    @DisplayName("옵션 그룹 fetch join 조회")
    void findWithMenuOption() throws Exception {
        // given
        Shop shop = Shop.builder()
                .name("네네치킨")
                .icon("icon.png")
                .banner("banner.png")
                .ownerNotice("사장님 공지")
                .businessHours("매일")
                .callNumber("010-1234-1234")
                .address("주소")
                .orderTypes("배달")
                .build();
        em.persist(shop);

        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .shop(shop)
                .position(1)
                .build();
        em.persist(menuOptionGroup);

        em.persist(MenuOption.builder()
                .menuOptionGroup(menuOptionGroup)
                .build());

        em.flush();
        em.clear();

        // when
        MenuOptionGroup findMenuOptionGroup = menuOptionGroupRepository.findWithMenuOptionById(menuOptionGroup.getId()).get();

        // then
        PersistenceUnitUtil persistenceUnitUtil = emf.getPersistenceUnitUtil();
        assertThat(persistenceUnitUtil.isLoaded(findMenuOptionGroup.getMenuOptions().get(0))).isTrue();
    }
}