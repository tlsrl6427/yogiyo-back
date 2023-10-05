package toy.yogiyo.core.menuoption.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOptionGroup {

    @Id
    @GeneratedValue
    @Column(name = "menu_option_group_id")
    private Long id;

    private String name;
    private Integer position;
    private Integer count;
    private Boolean isPossibleCount;

    @Enumerated(EnumType.STRING)
    private OptionType optionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Builder.Default
    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menuOptionGroup")
    private List<MenuOption> menuOptions = new ArrayList<>();

    @Builder.Default
    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menuOptionGroup")
    private List<MenuOptionGroupMenu> menus = new ArrayList<>();

    public void createMenuOption(String content, int price) {
        MenuOption menuOption = MenuOption.builder()
                .content(content)
                .price(price)
                .menuOptionGroup(this)
                .build();

        getMenuOptions().add(menuOption);
    }

    public void changePosition(int position) {
        this.position = position;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
