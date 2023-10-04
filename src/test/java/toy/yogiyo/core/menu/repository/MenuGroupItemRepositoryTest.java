package toy.yogiyo.core.menu.repository;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.domain.MenuGroupItem;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MenuGroupItemRepositoryTest {

    @Autowired
    MenuGroupItemRepository menuGroupItemRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("메뉴 목록 조회")
    void findMenus() throws Exception {
        // given
        MenuGroup menuGroup = MenuGroup.builder()
                .name("순살 메뉴")
                .content("뼈가 없습니다")
                .build();

        em.persist(menuGroup);

        for (int i = 0; i < 5; i++) {
            Menu menu = Menu.builder()
                    .name("메뉴 " + i)
                    .content("메뉴 " + i + " 설명")
                    .price(10000)
                    .build();
            em.persist(menu);

            MenuGroupItem menuGroupItem = MenuGroupItem.builder()
                    .position(i + 1)
                    .menu(menu)
                    .menuGroup(menuGroup)
                    .build();
            em.persist(menuGroupItem);
        }

        // when
        List<MenuGroupItem> menuGroupItems = menuGroupItemRepository.findMenus(menuGroup.getId());

        // then
        for (int i = 0; i < menuGroupItems.size(); i++) {
            MenuGroupItem menuGroupItem = menuGroupItems.get(i);
            assertThat(menuGroupItem.getMenu().getName()).isEqualTo("메뉴 " + i);
        }
    }

    @Test
    @DisplayName("메뉴 조회")
    void findByMenuId() throws Exception {
        // given
        MenuGroup menuGroup = MenuGroup.builder()
                .name("순살 메뉴")
                .content("뼈가 없습니다")
                .build();
        em.persist(menuGroup);

        Menu menu = Menu.builder()
                .name("메뉴")
                .content("메뉴 설명")
                .price(10000)
                .build();
        em.persist(menu);

        MenuGroupItem menuGroupItem = MenuGroupItem.builder()
                .position(1)
                .menu(menu)
                .menuGroup(menuGroup)
                .build();
        em.persist(menuGroupItem);

        // when
        MenuGroupItem findMenuGroupItem = menuGroupItemRepository.findByMenuId(menu.getId()).get();

        // then
        assertThat(findMenuGroupItem).isEqualTo(menuGroupItem);
    }

    @Test
    @DisplayName("메뉴 정렬 마지막 순서 조회")
    void addMenu() throws Exception {
        // given
        MenuGroup menuGroup = MenuGroup.builder()
                .name("순살 메뉴")
                .content("뼈가 없습니다")
                .build();

        em.persist(menuGroup);

        for (int i = 0; i < 5; i++) {
            Menu menu = Menu.builder()
                    .name("메뉴 " + i)
                    .content("메뉴 " + i + " 설명")
                    .price(10000)
                    .build();
            em.persist(menu);

            MenuGroupItem menuGroupItem = MenuGroupItem.builder()
                    .position(i + 1)
                    .menu(menu)
                    .menuGroup(menuGroup)
                    .build();
            em.persist(menuGroupItem);
        }

        // when
        Integer maxOrder = menuGroupItemRepository.findMaxOrder(menuGroup.getId());

        // then
        assertThat(maxOrder).isEqualTo(5);
    }

}