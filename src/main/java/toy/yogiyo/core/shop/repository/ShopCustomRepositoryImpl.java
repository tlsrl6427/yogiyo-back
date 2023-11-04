package toy.yogiyo.core.shop.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import toy.yogiyo.api.ShopScrollListRequest;
import toy.yogiyo.core.Like.domain.QLike;
import toy.yogiyo.core.category.domain.QCategory;
import toy.yogiyo.core.category.domain.QCategoryShop;
import toy.yogiyo.core.shop.domain.QDeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.QShop;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

import static toy.yogiyo.core.Like.domain.QLike.like;
import static toy.yogiyo.core.Order.domain.QOrder.order;
import static toy.yogiyo.core.category.domain.QCategory.category;
import static toy.yogiyo.core.category.domain.QCategoryShop.categoryShop;
import static toy.yogiyo.core.shop.domain.QDeliveryPriceInfo.deliveryPriceInfo;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ShopCustomRepositoryImpl implements ShopCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Shop> scrollLikes(Long memberId, Long lastId) {
        return jpaQueryFactory
                .select(shop)
                .from(shop)
                .join(like).on(shop.id.eq(like.shop.id))
                .where(like.member.id.eq(memberId),
                        lastIdLt(lastId))
                .orderBy(shop.id.desc())
                .limit(6)
                .fetch();
    }

    @Override
    public List<Shop> scrollShopList(ShopScrollListRequest request) {

        OrderSpecifier<?> orderSpecifier;
        if(request.getSortOption() == null || request.getSortOption().isEmpty()){
            orderSpecifier = new OrderSpecifier<>(Order.DESC, shop.reviewNum);
        }else if(request.getSortOption().equals("CLOSEST")){
            orderSpecifier = sortByClosest(request.getLatitude(), request.getLongitude());
        }else{
            orderSpecifier = sortByOption(request.getSortOption());
        }

        return jpaQueryFactory
                .select(shop)
                .from(shop)
                .join(categoryShop).on(shop.id.eq(categoryShop.shop.id))
                .join(category).on(categoryShop.category.id.eq(category.id))
                .where(
                        categoryNameEq(request.getCategory()),
                        deliveryPriceLt(request.getDeliveryPrice()),
                        leastOrderPriceLt(request.getLeastOrderPrice())
                )
                .orderBy(orderSpecifier)
                .limit(6)
                .offset(request.getOffset()==null ? 0L : request.getOffset())
                .fetch();
    }

    private BooleanExpression leastOrderPriceLt(Integer leastOrderPrice) {
        return leastOrderPrice == null ? null : shop.minOrderPrice.loe(leastOrderPrice);
    }

    private BooleanExpression deliveryPriceLt(Integer deliveryPrice) {
        return deliveryPrice == null ? null : shop.minDeliveryPrice.loe(deliveryPrice);
    }


    private OrderSpecifier<?> sortByClosest(Double latitude, Double longitude) {
        return new OrderSpecifier<>(Order.ASC, getShopDistance(latitude, longitude));
    }

    private OrderSpecifier<?> sortByOption(String sortOption) {
        if(sortOption.equals("ORDER")){
            return new OrderSpecifier<>(Order.DESC, shop.orderNum);
        }else if(sortOption.equals("REVIEW")){
            return new OrderSpecifier<>(Order.DESC, shop.reviewNum);
        }
        else if(sortOption.equals("SCORE")){
            return new OrderSpecifier<>(Order.DESC, shop.totalScore);
        }
        return new OrderSpecifier<>(Order.DESC, shop.reviewNum);
    }

    /*private BooleanExpression shopDistanceLt(Double latitude, Double longitude, Integer meter) {
        if (latitude == null || longitude == null || meter == null) {
            return null;
        }

        return getShopDistance(latitude, longitude).lt(meter);
    }*/

    private NumberTemplate<Double> getShopDistance(Double latitude, Double longitude) {
        return Expressions.numberTemplate(
                Double.class,
                "st_distance_sphere({0}, {1})",
                Expressions.stringTemplate("point({0}, {1})", shop.longitude, shop.latitude),
                Expressions.stringTemplate("point({0}, {1})", longitude, latitude)
        );
    }

    private static BooleanExpression orderPriceMinLoe(Integer orderPrice) {
        return orderPrice == null ? null : deliveryPriceInfo.orderPrice.min().loe(orderPrice);
    }

    private static BooleanExpression deliveryPriceMinLoe(Integer deliveryPrice) {
        return deliveryPrice == null ? null :deliveryPriceInfo.deliveryPrice.min().loe(deliveryPrice);
    }

    private BooleanExpression categoryNameEq(String categoryName){
        return categoryName == null ? null : category.name.eq(categoryName);
    }

    private static BooleanExpression lastIdLt(Long lastId) {
        return lastId == null ? null : shop.id.lt(lastId);
    }
}
