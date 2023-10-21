package toy.yogiyo.core.shop.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessHours {

    @Id @GeneratedValue
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
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public void changeShop(Shop shop) {
        this.shop = shop;
    }

}
