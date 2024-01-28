package toy.yogiyo.core.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.menu.domain.Menu;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(indexes = @Index(name = "idx_order_id", columnList = "order_id"))
public class OrderItem extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price;
    private int quantity;
    private String menuName;
    private Long menuId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

    @OneToMany(mappedBy = "orderItem",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemOption> orderItemOptions = new ArrayList<>();

    @Builder
    public OrderItem(Long id, int price, int quantity, String menuName, List<OrderItemOption> orderItemOptions, Long menuId) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.menuName = menuName;
        this.orderItemOptions = orderItemOptions;
        this.menuId = menuId;
    }


    public void setOrder(Order order){
        this.order = order;
    }
}
