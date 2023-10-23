package toy.yogiyo.core.Order.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.yogiyo.core.Like.domain.QLike;
import toy.yogiyo.core.Order.domain.Order;

import java.util.List;

import static toy.yogiyo.core.Like.domain.QLike.like;
import static toy.yogiyo.core.Order.domain.QOrder.order;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Repository
@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

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
