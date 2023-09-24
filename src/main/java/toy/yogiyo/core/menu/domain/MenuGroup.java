package toy.yogiyo.core.menu.domain;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MenuGroup {

    @Id
    @GeneratedValue
    @Column(name = "food_section_id")
    private Long id;

    private String name;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public void changeInfo(MenuGroup param) {
        this.name = param.getName();
        this.content = param.getContent();
    }
}
