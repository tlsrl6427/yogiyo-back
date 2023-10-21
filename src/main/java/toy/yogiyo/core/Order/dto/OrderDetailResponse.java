package toy.yogiyo.core.Order.dto;

import lombok.*;
import toy.yogiyo.core.Address.domain.Address;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Order.domain.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderDetailResponse {

    private Long orderId;
    private Status status;
    private OrderType orderType;
    private String shopName;
    private Long shopId;
    private String orderNumber;
    private LocalDateTime orderTime;
    private List<OrderItem> orderItems;
    private int totalPrice;
    private int deliveryPrice;
    private int paymentPrice;
    private PaymentType paymentType;
    //private String phoneNumber;
    private Address address;
    private String requestMsg;
    private boolean requestDoor;
    private boolean requestSpoon;

    public static OrderDetailResponse from(Order order){
        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .orderType(order.getOrderType())
                .shopName(order.getShop().getName())
                .shopId(order.getShop().getId())
                .orderNumber(order.getOrderNumber())
                .orderTime(order.getCreatedAt())
                .orderItems(order.getOrderItems())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .paymentPrice(order.getTotalPaymentPrice())
                .paymentType(order.getPaymentType())
                .address(order.getAddress())
                .requestMsg(order.getRequestMsg())
                .requestDoor(order.isRequestDoor())
                .requestSpoon(order.isRequestSpoon())
                .build();
    }

}
