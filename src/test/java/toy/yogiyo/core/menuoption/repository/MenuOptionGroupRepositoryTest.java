package toy.yogiyo.core.menuoption.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.domain.OptionGroupLinkMenu;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MenuOptionGroupRepositoryTest {

    @Autowired
    MenuOptionGroupRepository menuOptionGroupRepository;

    @Autowired
    EntityManager em;

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
    @DisplayName("옵션 그룹 전체 조회")
    void findAllByShopId() throws Exception {
        // given
        Shop shop = Shop.builder()
                .name("네네치킨")
                .icon("icon.png")
                .banner("banner.png")
                .ownerNotice("사장님 공지")
                .businessHours("매일")
                .callNumber("010-1234-1234")
                .address("주소")
                .build();
        em.persist(shop);

        Menu menu = Menu.builder().build();
        em.persist(menu);

        for (int i = 0; i < 5; i++) {
            MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                    .shop(shop)
                    .position(i + 1)
                    .build();
            em.persist(menuOptionGroup);

            em.persist(OptionGroupLinkMenu.builder()
                    .menu(menu)
                    .menuOptionGroup(menuOptionGroup)
                    .build());

            for (int j = 0; j < 3; j++) {
                em.persist(MenuOption.builder()
                        .menuOptionGroup(menuOptionGroup)
                        .build());
            }
        }

        em.flush();
        em.clear();

        // when
        List<MenuOptionGroup> findOptionGroups = menuOptionGroupRepository.findAllByShopId(shop.getId());

        // then
        assertThat(findOptionGroups.size()).isEqualTo(5);
        assertThat(findOptionGroups.get(0).getMenuOptions().size()).isEqualTo(3);
        assertThat(findOptionGroups.get(0).getLinkMenus().size()).isEqualTo(1);
    }
}