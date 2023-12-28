package toy.yogiyo.core.shop.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_shop_id", columnList = "shop_id"))
public class BusinessHours {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_hours_id")
    private Long id;

    @Enumerated
    private Days dayOfWeek;
    private boolean isOpen;
    private LocalTime openTime;
    private LocalTime closeTime;

    private LocalTime breakTimeStart;
    private LocalTime breakTimeEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    public void setShop(Shop shop) {
        this.shop = shop;
    }

}
