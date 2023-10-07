package toy.yogiyo.core.menuoption.domain;

import lombok.*;
import toy.yogiyo.core.menu.domain.Menu;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MenuOptionGroupMenu {

    @Id
    @GeneratedValue
    @Column(name = "menu_option_group_menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_Id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_group_Id")
    private MenuOptionGroup menuOptionGroup;

}
