package toy.yogiyo.core.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.like.domain.Like;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeCustomRepository {

    @Query("select l from Like l where l.member.id=:memberId and l.shop.id=:shopId")
    Optional<Like> findByMemberAndShop(@Param("memberId") Long memberId, @Param("shopId") Long shopId);

}
