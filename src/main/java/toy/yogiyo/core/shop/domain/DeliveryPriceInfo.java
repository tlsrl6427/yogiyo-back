package toy.yogiyo.core.shop.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(indexes = @Index(name = "idx_shop_id", columnList = "shop_id"))
public class DeliveryPriceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_price_info_id")
    private Long id;

    private int orderPrice;
    private int deliveryPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    public DeliveryPriceInfo() {
    }

    public DeliveryPriceInfo(int orderPrice, int deliveryPrice) {
        this.orderPrice = orderPrice;
        this.deliveryPrice = deliveryPrice;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

}
