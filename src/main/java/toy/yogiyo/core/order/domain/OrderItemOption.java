package toy.yogiyo.core.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItemOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String optionName;
    private int price;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Builder
    public OrderItemOption(String optionName, int price) {
        this.optionName = optionName;
        this.price = price;
    }

    public void setOrderItem(OrderItem orderItem){
        this.orderItem = orderItem;
    }
}
