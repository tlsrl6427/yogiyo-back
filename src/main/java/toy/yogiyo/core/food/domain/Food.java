package toy.yogiyo.core.food.domain;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food {

    @Id @GeneratedValue
    @Column(name = "food_id")
    private Long id;

    private String name;
    private String content;
    private String picture;
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public void changePicture(String picture) {
        this.picture = picture;
    }

    public void changeInfo(Food updateParam) {
        this.name = updateParam.getName();
        this.content = updateParam.getContent();
        this.price = updateParam.getPrice();
    }
}
