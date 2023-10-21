package toy.yogiyo.core.menuoption.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;

import java.util.List;


@DataJpaTest
class MenuOptionGroupPermissionEvaluatorTest {

    @Autowired
    EntityManager em;

    @Autowired
    MenuOptionGroupPermissionEvaluator menuOptionGroupPermissionEvaluator;

    @Test
    @DisplayName("옵션 그룹 수정 권한 체크")
    void hasWritePermission() {
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

        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .shop(shop)
                .name("옵션그룹")
                .build();
        em.persist(menuOptionGroup);

        Authentication authentication = new UsernamePasswordAuthenticationToken(owner, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // when
        boolean hasWritePermission = menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, menuOptionGroup.getId());

        // then
        Assertions.assertThat(hasWritePermission).isTrue();
    }

    @TestConfiguration
    public static class Config {

        @Autowired
        EntityManager em;

        @Bean
        public MenuOptionGroupPermissionEvaluator menuOptionGroupPermissionEvaluator() {
            return new MenuOptionGroupPermissionEvaluator(em);
        }

    }
}