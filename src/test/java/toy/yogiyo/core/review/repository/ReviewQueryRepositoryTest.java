package toy.yogiyo.core.review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import toy.yogiyo.common.config.TestQuerydslConfiguration;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.dto.ReviewQueryCondition;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(TestQuerydslConfiguration.class)
@DataJpaTest
class ReviewQueryRepositoryTest {

    @Autowired
    ReviewQueryRepository reviewQueryRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void init() {
        for (int i = 1; i <= 20; i++) {
            Review review = Review.builder()
                    .content("리뷰 " + i)
                    .shopId(1L)
                    .tasteScore(i * 0.1f)
                    .build();
            em.persist(review);
        }
    }

    @Test
    @DisplayName("점주 리뷰 확인 - 최신순")
    void shopReviewScrollLatest() {
        // given
        ReviewQueryCondition condition = ReviewQueryCondition.builder()
                .sort(ReviewQueryCondition.Sort.LATEST)
                .status(ReviewQueryCondition.Status.ALL)
                .build();

        // when
        Scroll<Review> reviewScroll = reviewQueryRepository.shopReviewScroll(1L, condition);

        // then
        List<Review> content = reviewScroll.getContent();
        for (int i = 0; i < content.size()-1; i++) {
            assertThat(content.get(i).getCreatedAt().compareTo(content.get(i+1).getCreatedAt()))
                    .isGreaterThanOrEqualTo(0);
        }
    }
    
    @Test
    @DisplayName("점주 리뷰 확인 - 별점 높은순")
    void shopReviewScrollRatingHigh() {
        ReviewQueryCondition condition = ReviewQueryCondition.builder()
                .sort(ReviewQueryCondition.Sort.RATING_HIGH)
                .status(ReviewQueryCondition.Status.ALL)
                .build();

        // when
        Scroll<Review> reviewScroll = reviewQueryRepository.shopReviewScroll(1L, condition);

        // then
        List<Review> content = reviewScroll.getContent();
        for (int i = 0; i < content.size()-1; i++) {
            assertThat(content.get(i).getTasteScore())
                    .isGreaterThanOrEqualTo(content.get(i+1).getTasteScore());
        }
    }

    @Test
    @DisplayName("점주 리뷰 확인 - 별점 낮은순")
    void shopReviewScrollRatingLow() {
        ReviewQueryCondition condition = ReviewQueryCondition.builder()
                .sort(ReviewQueryCondition.Sort.RATING_LOW)
                .status(ReviewQueryCondition.Status.ALL)
                .build();

        // when
        Scroll<Review> reviewScroll = reviewQueryRepository.shopReviewScroll(1L, condition);

        // then
        List<Review> content = reviewScroll.getContent();
        for (int i = 0; i < content.size()-1; i++) {
            assertThat(content.get(i).getTasteScore())
                    .isLessThanOrEqualTo(content.get(i+1).getTasteScore());
        }
    }

    @TestConfiguration
    static class Config {

        @Autowired
        JPAQueryFactory queryFactory;

        @Bean
        public ReviewQueryRepository reviewQueryRepository() {
            return new ReviewQueryRepository(queryFactory);
        }

    }
}