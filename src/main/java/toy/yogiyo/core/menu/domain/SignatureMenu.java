package toy.yogiyo.core.menu.domain;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignatureMenu {

    @Id @GeneratedValue
    @Column(name = "signature_menu_id")
    private Long id;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    public void changePosition(int position) {
        this.position = position;
    }

}
