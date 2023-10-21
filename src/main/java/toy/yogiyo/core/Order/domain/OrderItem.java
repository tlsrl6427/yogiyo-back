package toy.yogiyo.core.Order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import toy.yogiyo.common.domain.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItem extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    private int price;
    private int quantity;
    private String menuName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(mappedBy = "orderItem",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemOption> orderItemOptions = new ArrayList<>();

    @Builder
    public OrderItem(Long id, int price, int quantity, String menuName, List<OrderItemOption> orderItemOptions) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.menuName = menuName;
        this.orderItemOptions = orderItemOptions;
    }


    public void setOrder(Order order){
        this.order = order;
    }
}
