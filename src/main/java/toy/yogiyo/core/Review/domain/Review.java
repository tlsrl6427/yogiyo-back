package toy.yogiyo.core.Review.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Order.domain.Order;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float tasteScore;
    private float quantityScore;
    private float deliveryScore;

    private String content;
    private String ownerReply;
    private LocalDateTime ownerReplyCreatedAt;

    private Long shopId;
    private String shopName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(mappedBy = "review")
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Builder
    public Review(Long id, float tasteScore, float quantityScore, float deliveryScore, String content, String ownerReply, LocalDateTime ownerReplyCreatedAt, Long shopId, String shopName, Member member, Order order, List<ReviewImage> reviewImages) {
        this.id = id;
        this.tasteScore = tasteScore;
        this.quantityScore = quantityScore;
        this.deliveryScore = deliveryScore;
        this.content = content;
        this.ownerReply = ownerReply;
        this.ownerReplyCreatedAt = ownerReplyCreatedAt;
        this.shopId = shopId;
        this.shopName = shopName;
        this.member = member;
        this.order = order;
        this.reviewImages = reviewImages;
    }

    public void changeOwnerReply(String reply) {
        if (this.ownerReply == null) {
            this.ownerReplyCreatedAt = LocalDateTime.now();
        }
        this.ownerReply = reply;
    }
}
