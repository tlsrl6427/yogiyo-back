package toy.yogiyo.core.menuoption.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class MenuOptionRepositoryTest {

    @Autowired
    MenuOptionRepository menuOptionRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("옵션 정렬 마지막 순서 조회")
    void findMaxOrder() throws Exception {
        // given
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder().build();
        em.persist(menuOptionGroup);

        for (int i = 0; i < 5; i++) {
            em.persist(MenuOption.builder()
                    .menuOptionGroup(menuOptionGroup)
                    .position(i + 1)
                    .build());
        }

        // when
        Integer maxOrder = menuOptionRepository.findMaxOrder(menuOptionGroup.getId());

        // then
        assertThat(maxOrder).isEqualTo(5);
    }

    @Test
    @DisplayName("옵션 그룹 내 옵션 전체 조회")
    void findAllByMenuOptionGroupId() throws Exception {
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

        for (int i = 0; i < 5; i++) {
            em.persist(MenuOption.builder()
                    .menuOptionGroup(menuOptionGroup)
                    .position(i + 1)
                    .build());
        }

        em.flush();
        em.clear();

        // when
        List<MenuOption> findOptions = menuOptionRepository.findAllByMenuOptionGroupId(menuOptionGroup.getId());

        // then
        assertThat(findOptions.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("옵션 그룹 내 옵션 전체 삭제")
    void deleteAll() throws Exception {
        // given
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder().build();
        em.persist(menuOptionGroup);

        for (int i = 0; i < 5; i++) {
            em.persist(MenuOption.builder()
                    .menuOptionGroup(menuOptionGroup)
                    .position(i + 1)
                    .build());
        }

        // when
        int deleteCount = menuOptionRepository.deleteAllByGroupId(menuOptionGroup.getId());

        // then
        assertThat(deleteCount).isEqualTo(5);
    }
}