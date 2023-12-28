package toy.yogiyo.core.review.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.order.domain.Order;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_member_id", columnList = "member_id"),
        @Index(name = "idx_order_id", columnList = "order_id")
})
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double tasteScore;
    private double quantityScore;
    private double deliveryScore;
    private double totalScore;

    private String content;
    private String ownerReply;
    private LocalDateTime ownerReplyCreatedAt;

    private Long shopId;
    private String shopName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Builder
    public Review(Long id, double tasteScore, double quantityScore, double deliveryScore, double totalScore, String content, String ownerReply, LocalDateTime ownerReplyCreatedAt, Long shopId, String shopName, Member member, Order order, List<ReviewImage> reviewImages) {
        this.id = id;
        this.tasteScore = tasteScore;
        this.quantityScore = quantityScore;
        this.deliveryScore = deliveryScore;
        this.totalScore = totalScore;
        this.content = content;
        this.ownerReply = ownerReply;
        this.ownerReplyCreatedAt = ownerReplyCreatedAt;
        this.shopId = shopId;
        this.shopName = shopName;
        this.member = member;
        this.order = order;
        this.reviewImages = reviewImages;
    }

    public void updateOwnerReply(String reply) {
        if (this.ownerReply == null) {
            this.ownerReplyCreatedAt = LocalDateTime.now();
        }
        this.ownerReply = reply;
    }
}
