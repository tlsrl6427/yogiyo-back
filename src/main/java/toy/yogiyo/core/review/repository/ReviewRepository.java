package toy.yogiyo.core.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

}
