package toy.yogiyo.core.category;

import toy.yogiyo.core.shop.Shop;

import javax.persistence.*;

@Entity
public class CategoryShop {

    @Id
    @GeneratedValue
    @Column(name = "category_shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;
}
