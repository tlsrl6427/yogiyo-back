package toy.yogiyo.core.menuoption.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_option_id")
    private Long id;

    private String content;
    private Integer price;
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_group_id")
    private MenuOptionGroup menuOptionGroup;

    public void updatePosition(int position) {
        this.position = position;
    }

    public void updateInfo(MenuOption param) {
        this.content = param.getContent();
        this.price = param.getPrice();
    }
}
