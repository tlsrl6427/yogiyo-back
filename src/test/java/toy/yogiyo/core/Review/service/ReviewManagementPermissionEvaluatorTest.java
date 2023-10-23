package toy.yogiyo.core.Review.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
import toy.yogiyo.core.Review.domain.Review;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.EntityManager;
import java.util.List;

@Import(TestQuerydslConfiguration.class)
@DataJpaTest
class ReviewManagementPermissionEvaluatorTest {

    @Autowired
    EntityManager em;

    @Autowired
    ReviewManagementPermissionEvaluator reviewManagementPermissionEvaluator;

    @Test
    @DisplayName("옵션 수정 권한 체크")
    void hasPermission() {
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

        Review review = Review.builder()
                .shopId(shop.getId())
                .content("리뷰")
                .build();
        em.persist(review);

        Authentication authentication = new UsernamePasswordAuthenticationToken(owner, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // when
        boolean hasPermission = reviewManagementPermissionEvaluator.hasPermission(authentication, review.getId());

        // then
        Assertions.assertThat(hasPermission).isTrue();
    }

    @TestConfiguration
    public static class Config {
        @Autowired
        JPAQueryFactory jpaQueryFactory;

        @Bean
        public ReviewManagementPermissionEvaluator reviewManagementPermissionEvaluator() {
            return new ReviewManagementPermissionEvaluator(jpaQueryFactory);
        }
    }
}