package toy.yogiyo.core.category.domain;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    public void setCategory(Category category, Shop shop) {
        this.category = category;
        this.shop = shop;

        shop.getCategoryShop().add(this);
    }
}
