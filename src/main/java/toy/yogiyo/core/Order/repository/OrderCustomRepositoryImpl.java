package toy.yogiyo.core.Order.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.yogiyo.core.Order.domain.Order;

import java.util.List;

import static toy.yogiyo.core.Order.domain.QOrder.order;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Repository
@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    /*@Override
    public List<OrderHistory> scrollOrderDetails(Long memberId, Long lastId) {
        return jpaQueryFactory.select(Projections.constructor(OrderHistory.class,
                            order.id.as("orderId"),
                            order.createdAt.as("orderTime"),
                            order.orderType,
                            order.status,
                            shop.name.as("shopName"),
                            shop.id.as("shopId"),
                            //menuName
                            shop.icon.as("shopImg")
                            //menuCount
                            //totalMenuCount
                        ))
                .from(order)
                .leftJoin(order.shop, shop)
                .where(order.member.id.eq(memberId))
                .orderBy(member.id.desc())
                .offset(lastId)
                .limit(6L).fetch();
    }*/

    @Override
    public List<Order> scrollOrders(Long memberId, Long lastId) {
        return jpaQueryFactory
                .select(order)
                .from(order)
                .leftJoin(order.shop, shop).fetchJoin()
                .where(order.member.id.eq(memberId),
                        lastIdLt(lastId))
                .orderBy(order.id.desc())
                .limit(6L).fetch();
    }

    private static BooleanExpression lastIdLt(Long lastId) {
        return lastId == -1 ? null : order.id.lt(lastId);
    }
}
