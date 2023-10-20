package toy.yogiyo.core.Order.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.Address.domain.Address;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Order.dto.OrderCreateRequest;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Orders")
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    private String orderNumber;
    private int totalPrice;
    private int deliveryPrice;
    private int totalPaymentPrice;
    @Embedded
    private Address address;
    private String phoneNumber;
    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;
    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;
    private String requestMsg;
    private boolean requestDoor;
    private boolean requestSpoon;
    private Status status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Builder
    public Order(String orderNumber, int totalPrice, int deliveryPrice, int totalPaymentPrice, Address address, String phoneNumber, OrderType orderType, PaymentType paymentType, String requestMsg, boolean requestDoor, boolean requestSpoon, Status status, List<OrderItem> orderItems, Member member, Shop shop) {
        this.orderNumber = orderNumber;
        this.totalPrice = totalPrice;
        this.deliveryPrice = deliveryPrice;
        this.totalPaymentPrice = totalPaymentPrice;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.orderType = orderType;
        this.paymentType = paymentType;
        this.requestMsg = requestMsg;
        this.requestDoor = requestDoor;
        this.requestSpoon = requestSpoon;
        this.status = status;
        this.orderItems = orderItems;
        this.member = member;
        this.shop = shop;
    }

    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
    }

    public static Order createOrder(Member member, Shop shop, OrderCreateRequest request){
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear()%100;
        String month = now.getMonth().name().toUpperCase().substring(0, 3);
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        String temp = second+month+hour+"_"+year+minute;

        Order order = Order.builder()
                .orderNumber(temp)
                .totalPrice(request.getTotalPrice())
                .deliveryPrice(request.getDeliveryPrice())
                .totalPaymentPrice(request.getTotalPaymentPrice())
                .address(request.getAddress())
                .orderType(request.getOrderType())
                .paymentType(request.getPaymentType())
                .requestMsg(request.getRequestMsg())
                .requestDoor(request.isRequestDoor())
                .requestSpoon(request.isRequestSpoon())
                .status(Status.DONE)
                .orderItems(request.getOrderItems())
                .member(member)
                .shop(shop)
                .build();

        // OrderItem-OrderItemOption 연관관계 추가
        request.getOrderItems()
                .forEach(
                        orderItem -> orderItem.getOrderItemOptions()
                                .forEach(orderItemOption -> orderItemOption.setOrderItem(orderItem))
                );

        // Order-OrderItem 연관관계 추가
        request.getOrderItems()
                .forEach(orderItem -> orderItem.setOrder(order));

        return order;
    }


}
