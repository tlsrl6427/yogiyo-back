package toy.yogiyo.core.deliveryplace.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(indexes = @Index(name = "idx_delivery_place_id", columnList = "delivery_place_id"))
public class DeliveryPriceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_price_info_id")
    private Long id;

    private int orderPrice;
    private int deliveryPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_place_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private DeliveryPlace deliveryPlace;

    public DeliveryPriceInfo() {
    }

    public DeliveryPriceInfo(int orderPrice, int deliveryPrice) {
        this.orderPrice = orderPrice;
        this.deliveryPrice = deliveryPrice;
    }

}
