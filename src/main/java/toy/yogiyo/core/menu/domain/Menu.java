package toy.yogiyo.core.menu.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.core.menuoption.domain.OptionGroupLinkMenu;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_menu_group_id", columnList = "menu_group_id"))
@DynamicInsert
public class Menu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    private String name;
    private String content;
    @ColumnDefault("'/images/default.jpg'")
    private String picture;
    private Integer price;

    private Integer position;
    @ColumnDefault("'SHOW'")
    @Enumerated(EnumType.STRING)
    private Visible visible;
    private LocalDateTime soldOutUntil;

    private long reviewNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MenuGroup menuGroup;

    @Builder.Default
    @OneToMany(mappedBy = "menu", cascade = CascadeType.REMOVE)
    private List<OptionGroupLinkMenu> linkMenus = new ArrayList<>();

    public void updatePicture(String picture) {
        this.picture = picture;
    }

    public void updateInfo(Menu updateParam) {
        this.name = updateParam.getName();
        this.content = updateParam.getContent();
        this.price = updateParam.getPrice();
        this.visible = updateParam.getVisible();
    }

    public void updatePosition(int position) {
        this.position = position;
    }

    public void updateMenuGroup(MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
    }


    public void decreaseReviewNum() {
        this.reviewNum--;
    }

    public void increaseReviewNum() {
        this.reviewNum++;
    }

    public void updateVisible(Visible visible, LocalDateTime soldOutUntil) {
        this.visible = visible;
        this.soldOutUntil = soldOutUntil;
    }
}
