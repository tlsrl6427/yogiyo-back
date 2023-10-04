package toy.yogiyo.core.menuoption.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;

import javax.persistence.EntityManager;

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