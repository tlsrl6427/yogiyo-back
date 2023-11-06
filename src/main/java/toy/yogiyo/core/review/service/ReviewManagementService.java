package toy.yogiyo.core.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.exception.ExpiredException;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.repository.ReviewRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewManagementService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public void reply(Long reviewId, String ownerReply) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUNT));

        validateWithin90Days(review.getCreatedAt());

        review.changeOwnerReply(ownerReply);
    }

    @Transactional
    public void deleteReply(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUNT));

        review.changeOwnerReply(null);
    }

    private void validateWithin90Days(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        if (dateTime.isBefore(now.minusDays(90))) {
            throw new ExpiredException(ErrorCode.REVIEW_EXPIRED);
        }
    }

}
