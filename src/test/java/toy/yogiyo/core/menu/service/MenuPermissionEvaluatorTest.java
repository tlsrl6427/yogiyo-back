package toy.yogiyo.core.menu.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import toy.yogiyo.common.config.TestQuerydslConfiguration;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;
import java.util.List;


@DataJpaTest
@Import(TestQuerydslConfiguration.class)
class MenuPermissionEvaluatorTest {

    @Autowired
    EntityManager em;

    @Autowired
    MenuPermissionEvaluator menuPermissionEvaluator;
    
    @Test
    @DisplayName("메뉴 수정 권한 체크")
    void hasWritePermission() throws Exception {
        // given
        Owner owner = Owner.builder()
                .nickname("점주")
                .build();
        em.persist(owner);

        Shop shop = Shop.builder()
                .owner(owner)
                .name("가게1")
                .build();
        em.persist(shop);

        MenuGroup menuGroup = MenuGroup.builder()
                .shop(shop)
                .name("메뉴그룹")
                .build();
        em.persist(menuGroup);

        Menu menu = Menu.builder()
                .name("메뉴")
                .menuGroup(menuGroup)
                .build();
        em.persist(menu);

        Authentication authentication = new UsernamePasswordAuthenticationToken(owner, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // when
        boolean hasWritePermission = menuPermissionEvaluator.hasWritePermission(authentication, menu.getId());

        // then
        Assertions.assertThat(hasWritePermission).isTrue();
    }

    @TestConfiguration
    public static class Config {
        @Autowired
        EntityManager em;
        
        @Bean
        public MenuPermissionEvaluator menuPermissionEvaluator() {
            return new MenuPermissionEvaluator(em);
        }
    }

}