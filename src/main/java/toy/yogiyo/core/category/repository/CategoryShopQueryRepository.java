package toy.yogiyo.core.category.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.dto.CategoryShopCondition;

import javax.persistence.EntityManager;
import java.util.List;

import static toy.yogiyo.core.category.domain.QCategoryShop.categoryShop;
import static toy.yogiyo.core.shop.domain.QShop.shop;


@Repository
public class CategoryShopQueryRepository {

    private final JPAQueryFactory queryFactory;

    public CategoryShopQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public SliceImpl<CategoryShop> findAround(CategoryShopCondition condition, Pageable pageable) {
        List<CategoryShop> result = queryFactory
                .selectFrom(categoryShop)
                .join(categoryShop.shop, shop).fetchJoin()
                .where(categoryEq(condition.getCategoryId()),
                        shopNameContains(condition.getKeyword()),
                        shopDistanceLt(condition.getLatitude(), condition.getLongitude(), 2000))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (result.size() > pageable.getPageSize()) {
            hasNext = true;
            result.remove(result.size()-1);
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId == null ? null : categoryShop.category.id.eq(categoryId);
    }

    private BooleanExpression shopNameContains(String keyword) {
        return keyword == null ? null : categoryShop.shop.name.contains(keyword);
    }

    private BooleanExpression shopDistanceLt(double latitude, double longitude, double meter) {
        NumberTemplate<Double> numberTemplate = Expressions.numberTemplate(Double.class, "st_distance_sphere({0}, {1})",
                Expressions.stringTemplate("point({0}, {1})",
                        categoryShop.shop.longitude, categoryShop.shop.latitude),
                Expressions.stringTemplate("point({0}, {1})",
                        longitude, latitude));

        return numberTemplate.lt(meter);
    }

}
