package toy.yogiyo.core.menu.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;
import java.util.List;


@DataJpaTest
class MenuGroupRepositoryTest {

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("가게 메뉴 그룹 조회")
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
                .orderTypes("배달")
                .build();
        em.persist(shop);

        for (int i = 0; i < 5; i++) {
            MenuGroup menuGroup = MenuGroup.builder()
                    .name("순살 메뉴" + i)
                    .content("뼈가 없어 먹기 편해요" + i)
                    .shop(shop)
                    .build();
            em.persist(menuGroup);
        }

        // when
        List<MenuGroup> findMenuGroups = menuGroupRepository.findAllByShopId(shop.getId());

        // then
        Assertions.assertThat(findMenuGroups.size()).isEqualTo(5);
    }

}