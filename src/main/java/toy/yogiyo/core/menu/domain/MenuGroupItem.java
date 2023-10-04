package toy.yogiyo.core.menu.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MenuGroupItem {

    @Id
    @GeneratedValue
    @Column(name = "menu_group_item_id")
    private Long id;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_group_id")
    private MenuGroup menuGroup;

    public void changePosition(int position) {
        this.position = position;
    }
}
