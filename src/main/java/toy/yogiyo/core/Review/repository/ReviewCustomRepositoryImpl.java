package toy.yogiyo.core.Review.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import toy.yogiyo.core.Review.domain.QReview;
import toy.yogiyo.core.Review.domain.Review;

import java.util.List;

import static toy.yogiyo.core.Order.domain.QOrder.order;
import static toy.yogiyo.core.Review.domain.QReview.*;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Review> scrollByLastId(Long lastId) {
       return jpaQueryFactory.selectFrom(review)
               .leftJoin(review.order, order).fetchJoin()
               .where(lastIdLt(lastId))
               .orderBy(review.id.desc())
               .limit(6)
               .fetch();
    }

    private static BooleanExpression lastIdLt(Long lastId) {
        return lastId == null ? null : review.id.lt(lastId);
    }
}
