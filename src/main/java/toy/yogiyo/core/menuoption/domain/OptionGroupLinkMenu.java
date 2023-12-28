package toy.yogiyo.core.menuoption.domain;

import lombok.*;
import toy.yogiyo.core.menu.domain.Menu;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_menu_Id", columnList = "menu_Id"),
        @Index(name = "idx_menu_option_group_Id", columnList = "menu_option_group_Id")
})
public class OptionGroupLinkMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_group_link_menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_Id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_group_Id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MenuOptionGroup menuOptionGroup;

}
