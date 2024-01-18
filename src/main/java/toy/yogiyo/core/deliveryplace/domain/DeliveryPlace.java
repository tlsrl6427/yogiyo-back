package toy.yogiyo.core.deliveryplace.domain;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = { @Index(name = "idx_shop_id", columnList = "shop_id"), @Index(name = "idx_code", columnList = "code") })
public class DeliveryPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_place_id")
    private Long id;

    private String code;
    private String name;
    private int deliveryTime;

    private int minDeliveryPrice;
    private int maxDeliveryPrice;
    private int minOrderPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @Builder.Default
    @OneToMany(mappedBy = "deliveryPlace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryPriceInfo> deliveryPriceInfos = new ArrayList<>();

    public void updateDeliveryPrices(List<DeliveryPriceInfo> deliveryPriceInfos) {
        if (deliveryPriceInfos == null) return;

        if (!deliveryPriceInfos.isEmpty()) {
            this.minDeliveryPrice = deliveryPriceInfos.stream()
                    .min(Comparator.comparingInt(DeliveryPriceInfo::getDeliveryPrice)).get()
                    .getDeliveryPrice();
            this.maxDeliveryPrice = deliveryPriceInfos.stream()
                    .max(Comparator.comparingInt(DeliveryPriceInfo::getDeliveryPrice)).get()
                    .getDeliveryPrice();
            this.minOrderPrice = deliveryPriceInfos.stream()
                    .min(Comparator.comparingInt(DeliveryPriceInfo::getOrderPrice)).get()
                    .getOrderPrice();
        }

        this.deliveryPriceInfos.clear();
        for (DeliveryPriceInfo deliveryPriceInfo : deliveryPriceInfos) {
            this.deliveryPriceInfos.add(deliveryPriceInfo);
            deliveryPriceInfo.setDeliveryPlace(this);
        }
    }
}
