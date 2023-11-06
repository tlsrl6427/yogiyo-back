package toy.yogiyo.core.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.core.address.domain.Address;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.order.domain.*;
import toy.yogiyo.core.order.dto.OrderDetailResponse;
import toy.yogiyo.core.order.repository.OrderRepository;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    ShopRepository shopRepository;

    Order order;
    Member member;
    Shop shop;

    @BeforeEach
    void beforeEach(){
        member = Member.builder()
                .id(1L)
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .build();

        shop = Shop.builder()
                .id(1L)
                .name("BHC 행당점")
                .build();

        order = Order.builder()
                .orderNumber("ON123")
                .totalPrice(20000)
                .deliveryPrice(1000)
                .totalPaymentPrice(21000)
                .address(new Address("14582", "다산로 4길 57", "장미아파트 8동"))
                .orderType(OrderType.DELIVERY)
                .paymentType(PaymentType.CARD)
                .requestMsg("요청사항 없음")
                .requestDoor(true)
                .requestSpoon(false)
                .status(Status.DONE)
                .orderItems(
                        List.of(
                                OrderItem.builder()
                                        .price(12000)
                                        .quantity(1)
                                        .menuName("후라이드치킨")
                                        .orderItemOptions(
                                                List.of(
                                                        OrderItemOption.builder()
                                                                .optionName("양념추가")
                                                                .price(500)
                                                                .build()
                                                )
                                        ).build()
                        )
                )
                .member(member)
                .shop(shop)
                .build();
    }

    /*@Test
    void createOrder() {
        Member member = Member.builder()

                .build();
        Shop shop = Shop.builder()

                .build();

        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .totalPrice(20000)
                .deliveryPrice(1000)
                .totalPaymentPrice(21000)
                .address(new Address("14582", "다산로 4길 57", "장미아파트 8동"))
                .orderType(OrderType.DELIVERY)
                .paymentType(PaymentType.CARD)
                .requestMsg("요청사항 없음")
                .requestDoor(true)
                .requestSpoon(false)
                .shopId(1L)
                .orderItems(List.of(
                        new OrderItem(1L, 12000, 1, "후라이드치킨", null),
                        new OrderItem(2L, 8000, 1, "양념치킨", null),
                ))
                .build();

        given(shopRepository.findById(any())).willReturn(Optional.ofNullable(shop));

        orderService.createOrder(member, orderCreateRequest);

        assertAll(
                () -> verify(shopRepository).findById(any())
        );
    }*/

    @Test
    void getOrderHistory() {

//        List<Order> orders = List.of(
//                Order.builder()
//                        .
//                        .build();
//        );
    }

    @Test
    void getOrderDetail() {

        Long orderId = 1L;
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                .orderNumber("ON123")
                .address(new Address("14582", "다산로 4길 57", "장미아파트 8동"))
                .shopId(1L)
                .shopName("BHC 행당점")
                .orderTime(LocalDateTime.now())
                .orderItems(
                        List.of(
                                OrderItem.builder()
                                        .price(12000)
                                        .quantity(1)
                                        .menuName("후라이드치킨")
                                        .orderItemOptions(
                                                List.of(
                                                        OrderItemOption.builder()
                                                                .optionName("양념추가")
                                                                .price(500)
                                                                .build()
                                                )
                                        ).build()
                        )
                )
                .orderType(OrderType.DELIVERY)
                .status(Status.DONE)
                .requestMsg("요청사항 없음")
                .requestDoor(true)
                .requestSpoon(false)
                .orderType(OrderType.DELIVERY)
                .paymentType(PaymentType.CARD)
                .totalPrice(20000)
                .deliveryPrice(1000)
                .paymentPrice(21000)
                .build();

        given(orderRepository.findById(any())).willReturn(Optional.ofNullable(order));

        OrderDetailResponse result = orderService.getOrderDetail(member, orderId);

        assertAll(
                () -> verify(orderRepository).findById(any()),
                () -> assertThat(result.getOrderNumber()).isEqualTo(orderDetailResponse.getOrderNumber())
        );
    }
}