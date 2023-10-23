package toy.yogiyo.core.shop.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.yogiyo.core.Like.domain.QLike;
import toy.yogiyo.core.shop.domain.QShop;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

import static toy.yogiyo.core.Like.domain.QLike.like;
import static toy.yogiyo.core.Order.domain.QOrder.order;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Repository
@RequiredArgsConstructor
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

    private static BooleanExpression lastIdLt(Long lastId) {
        return lastId == -1 ? null : shop.id.lt(lastId);
    }
}
