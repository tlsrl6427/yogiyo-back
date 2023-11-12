package toy.yogiyo.core.review.repository;

import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.review.domain.Review;

import java.util.List;

public interface ReviewCustomRepository {

    List<Review> scrollByLastId(@Param("lastId") Long lastId);
}
