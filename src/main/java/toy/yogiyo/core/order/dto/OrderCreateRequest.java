package toy.yogiyo.core.order.dto;

import lombok.*;
import toy.yogiyo.core.address.domain.Address;
import toy.yogiyo.core.order.domain.OrderItem;
import toy.yogiyo.core.order.domain.OrderItemOption;
import toy.yogiyo.core.order.domain.OrderType;
import toy.yogiyo.core.order.domain.PaymentType;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "음식점 ID가 반드시 들어가야 합니다")
    private Long shopId;
    private Address address;
    private List<OrderItemDto> orderItems;
    private String requestMsg;
    private boolean requestDoor;
    private boolean requestSpoon;
    private OrderType orderType;
    private PaymentType paymentType;
    private int totalPrice;
    private int deliveryPrice;
    private int totalPaymentPrice;
    private String code;

    public List<OrderItem> convertOrderItemsToEntity() {
        return orderItems.stream()
                .map(OrderItemDto::toOrderItem)
                .collect(Collectors.toList());
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private int price;
        private int quantity;
        private String menuName;
        private Long menuId;
        private List<OrderItemOptionDto> orderItemOptions;

        private OrderItem toOrderItem() {
            return OrderItem.builder()
                    .price(price)
                    .quantity(quantity)
                    .menuName(menuName)
                    .menuId(menuId)
                    .orderItemOptions(orderItemOptions.stream()
                            .map(OrderItemOptionDto::toOrderItemOption)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemOptionDto {
        private String optionName;
        private int price;

        private OrderItemOption toOrderItemOption() {
            return OrderItemOption.builder()
                    .optionName(optionName)
                    .price(price)
                    .build();
        }
    }

}
