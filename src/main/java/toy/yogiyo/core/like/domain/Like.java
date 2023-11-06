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
@Table(name = "Likes")
public class Like extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
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
