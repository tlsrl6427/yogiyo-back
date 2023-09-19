package toy.yogiyo.core.category.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.dto.CategoryShopCondition;

import javax.persistence.EntityManager;
import java.util.List;

import static toy.yogiyo.core.category.domain.QCategoryShop.categoryShop;
import static toy.yogiyo.core.shop.domain.QDeliveryPriceInfo.*;
import static toy.yogiyo.core.shop.domain.QShop.shop;


@Repository
public class CategoryShopQueryRepository {

    private final JPAQueryFactory queryFactory;

    public CategoryShopQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Slice<CategoryShop> findAround(Long categoryId, CategoryShopCondition condition, Pageable pageable) {
        JPAQuery<CategoryShop> query = queryFactory
                .selectFrom(categoryShop)
                .join(categoryShop.shop, shop).fetchJoin();


        boolean shouldSortOrderPrice = shouldSortOrderPrice(pageable);
        if(shouldSortOrderPrice) {
            query.join(shop.deliveryPriceInfos, deliveryPriceInfo);
        }

        query.where(categoryEq(categoryId),
                        shopNameContains(condition.getKeyword()),
                        shopDistanceLt(condition.getLatitude(), condition.getLongitude(), 2000),
                        extractMinOrderPrice(shouldSortOrderPrice)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1);

        if(shouldSortOrderPrice) {
                query.orderBy(shopSort(pageable));
        }

        List<CategoryShop> result = query.fetch();

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
        return StringUtils.hasText(keyword) ? categoryShop.shop.name.contains(keyword) : null;
    }

    private BooleanExpression shopDistanceLt(Double latitude, Double longitude, Integer meter) {
        if (null == latitude || null == longitude || null == meter) {
            return null;
        }

        NumberTemplate<Double> numberTemplate = Expressions.numberTemplate(Double.class, "st_distance_sphere({0}, {1})",
                Expressions.stringTemplate("point({0}, {1})",
                        categoryShop.shop.longitude, categoryShop.shop.latitude),
                Expressions.stringTemplate("point({0}, {1})",
                        longitude, latitude));

        return numberTemplate.lt(meter);
    }

    private BooleanExpression extractMinOrderPrice(boolean shouldSortOrderPrice) {
        return shouldSortOrderPrice ? deliveryPriceInfo.orderPrice.eq(
                JPAExpressions.select(deliveryPriceInfo.orderPrice.min())
                        .from(deliveryPriceInfo)
                        .where(deliveryPriceInfo.shop.eq(categoryShop.shop))) : null;
    }

    private OrderSpecifier<?> shopSort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "orderPrice":
                        return new OrderSpecifier<>(direction, deliveryPriceInfo.deliveryPrice);
                }
            }
        }
        return null;
    }

    private boolean shouldSortOrderPrice(Pageable pageable) {
        if(pageable.getSort().isEmpty()){
            return false;
        }
        Sort.Order order = pageable.getSort().getOrderFor("orderPrice");

        if (null == order) {
            return false;
        }

        return true;
    }

}
