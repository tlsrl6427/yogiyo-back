package toy.yogiyo.core.Review.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.common.exception.ExpiredException;
import toy.yogiyo.core.Review.domain.Review;
import toy.yogiyo.core.Review.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewManagementServiceTest {

    @InjectMocks
    ReviewManagementService reviewManagementService;

    @Mock
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("답변 등록/수정")
    void updateReply() {
        // given
        Review review = Review.builder().build();
        review.setCreatedAt(LocalDateTime.now());
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        // when
        reviewManagementService.reply(1L, "답변");

        // then
        assertThat(review.getOwnerReply()).isEqualTo("답변");
    }

    @Test
    @DisplayName("리뷰 등록일로부터 90일이 지나면 등록/수정 안됨")
    void updateReplyFail90Days() {
        // given
        Review review = Review.builder().build();
        review.setCreatedAt(LocalDateTime.now().minusDays(91));
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        // when & then
        assertThatThrownBy(() -> reviewManagementService.reply(1L, "답변"))
                .isInstanceOf(ExpiredException.class);
    }

    @Test
    @DisplayName("답변 삭제")
    void deleteReply() {
        // given
        Review review = Review.builder().build();
        review.setCreatedAt(LocalDateTime.now());
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        // when
        reviewManagementService.deleteReply(1L);

        // then
        assertThat(review.getOwnerReply()).isNull();
    }

}