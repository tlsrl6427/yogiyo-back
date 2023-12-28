package toy.yogiyo.core.like.domain;

import lombok.*;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Likes", indexes = {
        @Index(name = "idx_member_id", columnList = "member_id"),
        @Index(name = "idx_shop_id", columnList = "shop_id")
})
public class Like extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    public static Like toLike(Member member, Shop shop){
        Like like = Like.builder()
                .member(member)
                .shop(shop)
                .build();
        member.addLike(like);
        return like;
    }
}
