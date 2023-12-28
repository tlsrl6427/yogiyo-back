package toy.yogiyo.core.menu.domain;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_shop_id", columnList = "shop_id"),
        @Index(name = "idx_menu_id", columnList = "menu_id")
})
public class SignatureMenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "signature_menu_id")
    private Long id;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Menu menu;

    public void updatePosition(int position) {
        this.position = position;
    }

}
