package toy.yogiyo.core.menu.domain;

import lombok.*;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(indexes = @Index(name = "idx_shop_id", columnList = "shop_id"))
public class MenuGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_group_id")
    private Long id;

    private String name;
    private String content;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @Builder.Default
    @OneToMany(mappedBy = "menuGroup", cascade = CascadeType.ALL)
    private List<Menu> menus = new ArrayList<>();

    public void updateInfo(MenuGroup param) {
        this.name = param.getName();
        this.content = param.getContent();
    }

    public void updatePosition(int position) {
        this.position = position;
    }
}
