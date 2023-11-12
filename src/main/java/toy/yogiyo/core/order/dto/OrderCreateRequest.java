package toy.yogiyo.core.order.dto;

import lombok.*;
import toy.yogiyo.core.address.domain.Address;
import toy.yogiyo.core.order.domain.OrderItem;
import toy.yogiyo.core.order.domain.OrderType;
import toy.yogiyo.core.order.domain.PaymentType;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderCreateRequest {

    private Long shopId;
    private Address address;
    private List<OrderItem> orderItems;
    private String requestMsg;
    private boolean requestDoor;
    private boolean requestSpoon;
    private OrderType orderType;
    private PaymentType paymentType;
    private int totalPrice;
    private int deliveryPrice;
    private int totalPaymentPrice;

}
