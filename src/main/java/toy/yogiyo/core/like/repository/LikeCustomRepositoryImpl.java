package toy.yogiyo.core.like.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.yogiyo.core.like.dto.LikeResponse;

import java.util.List;

import static toy.yogiyo.core.like.domain.QLike.like;
import static toy.yogiyo.core.shop.domain.QShop.shop;

@Repository
@RequiredArgsConstructor
public class LikeCustomRepositoryImpl implements LikeCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<LikeResponse> findLikesByMember(Long memberId) {
        return jpaQueryFactory
                .select(Projections.fields(LikeResponse.class,
                        like.id.as("likeId"),
                        shop.id.as("shopId"),
                        shop.name.as("shopName"),
                        shop.icon.as("shopImg"),
                        shop.totalScore.as("score")
                ))
                .from(shop)
                .join(like).on(shop.id.eq(like.shop.id))
                .where(like.member.id.eq(memberId))
                .orderBy(like.id.desc())
                .fetch();
    }
}
