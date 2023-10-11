package toy.yogiyo.core.menuoption.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.domain.OptionGroupLinkMenu;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class OptionGroupLinkMenuRepositoryTest {

    @Autowired
    OptionGroupLinkMenuRepository optionGroupLinkMenuRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("옵션 그룹 연결 메뉴 전체 삭제")
    void deleteAllByMenuOptionGroupId() throws Exception {
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

        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .shop(shop)
                .position(1)
                .build();
        em.persist(menuOptionGroup);

        for (int i = 0; i < 5; i++) {
            Menu menu = Menu.builder().build();
            em.persist(menu);
            em.persist(OptionGroupLinkMenu.builder()
                    .menuOptionGroup(menuOptionGroup)
                    .menu(menu)
                    .build());
        }

        // when
        int deleteCount = optionGroupLinkMenuRepository.deleteByMenuOptionGroupId(menuOptionGroup.getId());

        // then
        assertThat(deleteCount).isEqualTo(5);
    }

}