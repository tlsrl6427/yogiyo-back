package toy.yogiyo.core.menuoption.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_menu_option_group_id", columnList = "menu_option_group_id"))
public class MenuOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_option_id")
    private Long id;

    private String content;
    private Integer price;
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MenuOptionGroup menuOptionGroup;

    public void updatePosition(int position) {
        this.position = position;
    }

    public void updateInfo(MenuOption param) {
        this.content = param.getContent();
        this.price = param.getPrice();
    }
}
