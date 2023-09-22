package toy.yogiyo.core.category.domain;

import lombok.Getter;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;

@Getter
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

    public CategoryShop() {
    }

    public CategoryShop(Long id, Category category, Shop shop) {
        this.id = id;
        this.category = category;
        this.shop = shop;
    }

    public void setCategory(Category category, Shop shop) {
        this.category = category;
        this.shop = shop;

        shop.getCategoryShop().add(this);
    }
}
