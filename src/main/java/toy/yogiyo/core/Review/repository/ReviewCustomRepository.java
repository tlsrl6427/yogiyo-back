package toy.yogiyo.core.Review.repository;

import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.Review.domain.Review;

import java.util.List;

public interface ReviewCustomRepository {

    List<Review> scrollByLastId(@Param("lastId") Long lastId);
}
