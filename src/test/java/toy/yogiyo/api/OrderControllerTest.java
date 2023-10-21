package toy.yogiyo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.core.Address.domain.Address;
import toy.yogiyo.core.Order.domain.*;
import toy.yogiyo.core.Order.dto.*;
import toy.yogiyo.core.Order.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ExtendWith(RestDocumentationExtension.class)
class OrderControllerTest {

    @MockBean
    OrderService orderService;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    final String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInByb3ZpZGVyVHlwZSI6IkRFRkFVTFQiLCJleHAiOjE2OTQ5NjY4Mjh9.Ls1wnxU41I99ijXRyKfkYI2w3kd-Q_qA2QgCLgpDTKk";

    @BeforeEach
    void beforeEach(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider){
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("주문 생성")
    @Test
    void createOrder() throws Exception {
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .shopId(1L)
                .address(new Address("14582", "다산로 4길 57", "장미아파트 8동"))
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
                           /* new OrderItem(1L, 12000, 1, "후라이드치킨", null,
                                    List.of(
                                            new OrderItemOption(1L, "양념추가", 500, null)
                                    )
                            )*/
                        )
                )
                .requestMsg("요청사항 없음")
                .requestDoor(true)
                .requestSpoon(false)
                .orderType(OrderType.DELIVERY)
                .paymentType(PaymentType.CARD)
                .totalPrice(20000)
                .deliveryPrice(1000)
                .totalPaymentPrice(21000)
                .build();

        doNothing().when(orderService).createOrder(any(), any());

        mockMvc.perform(
                    post("/order/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", jwt)
                    .content(objectMapper.writeValueAsString(orderCreateRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("order/create",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                requestFields(
                                        fieldWithPath("shopId").type(JsonFieldType.NUMBER).description("음식점 ID"),
                                        fieldWithPath("address.zipcode").type(JsonFieldType.STRING).description("우편번호"),
                                        fieldWithPath("address.street").type(JsonFieldType.STRING).description("도로명 주소"),
                                        fieldWithPath("address.detail").type(JsonFieldType.STRING).description("상세주소"),

                                        //subsectionWithPath("orderItems[]").type(JsonFieldType.ARRAY).description("오더 아이템"),
                                        fieldWithPath("orderItems[].id").ignored(),
                                        fieldWithPath("orderItems[].createdAt").ignored(),
                                        fieldWithPath("orderItems[].updatedAt").ignored(),
                                        fieldWithPath("orderItems[].menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                                        fieldWithPath("orderItems[].price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("orderItems[].quantity").type(JsonFieldType.NUMBER).description("개수"),
                                        fieldWithPath("orderItems[].order").ignored(),

                                        fieldWithPath("orderItems[].orderItemOptions[].id").ignored(),
                                        fieldWithPath("orderItems[].orderItemOptions[].optionName").type(JsonFieldType.STRING).description("옵션 이름"),
                                        fieldWithPath("orderItems[].orderItemOptions[].price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("orderItems[].orderItemOptions[].orderItem").ignored(),

                                        fieldWithPath("requestMsg").type(JsonFieldType.STRING).description("요청사항"),
                                        fieldWithPath("requestDoor").type(JsonFieldType.BOOLEAN).description("문 앞에 두기 여부"),
                                        fieldWithPath("requestSpoon").type(JsonFieldType.BOOLEAN).description("수저여부"),
                                        fieldWithPath("orderType").type(JsonFieldType.STRING).description("주문방식"),
                                        fieldWithPath("paymentType").type(JsonFieldType.STRING).description("결제방식"),
                                        fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("총 금액"),
                                        fieldWithPath("deliveryPrice").type(JsonFieldType.NUMBER).description("배달 금액"),
                                        fieldWithPath("totalPaymentPrice").type(JsonFieldType.NUMBER).description("총 결제금액")
                                )
                        )
                );

        verify(orderService).createOrder(any(), any());
    }

    @DisplayName("주문내역 조회_스크롤")
    @Test
    void scrollOrderHistories() throws Exception {
        OrderHistoryResponse orderHistoryResponse = OrderHistoryResponse.builder()
                .orderHistories(
                        List.of(
                                OrderHistory.builder()
                                        .orderId(1L)
                                        .orderTime(LocalDateTime.now())
                                        .orderType(OrderType.DELIVERY)
                                        .status(Status.DONE)
                                        .shopId(1L)
                                        .shopName("BHC 행당점")
                                        .shopImg("img.jpg")
                                        .menuName("후라이드 치킨")
                                        .menuCount(2)
                                        .totalMenuCount(3)
                                        .build()
                        )
                )
                .lastId(5L)
                .hasNext(true)
                .build();

        given(orderService.getOrderHistory(any(), any())).willReturn(orderHistoryResponse);

        mockMvc.perform(
                    get("/order/scroll")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", jwt)
                    .param("lastId", "1")
                )
                .andExpect(status().isOk())
                //.andExpect(jsonPath("orderHistories").value(orderHistoryResponse.getOrderHistories()))
                .andExpect(jsonPath("lastId").value(orderHistoryResponse.getLastId()))
                .andExpect(jsonPath("hasNext").value(orderHistoryResponse.isHasNext()))
                .andDo(print())
                .andDo(
                        document("order/scroll",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("lastId").description("마지막 주문 ID")
                                ),
                                responseFields(
                                        //subsectionWithPath("orderHistories[]").type(JsonFieldType.ARRAY).description("오더 히스토리"),
                                        fieldWithPath("orderHistories[].orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                        fieldWithPath("orderHistories[].orderTime").type(JsonFieldType.STRING).description("주문시간"),
                                        fieldWithPath("orderHistories[].orderType").type(JsonFieldType.STRING).description("주문타입"),
                                        fieldWithPath("orderHistories[].status").type(JsonFieldType.STRING).description("주문상태"),
                                        fieldWithPath("orderHistories[].shopId").type(JsonFieldType.NUMBER).description("음식점 ID"),
                                        fieldWithPath("orderHistories[].shopName").type(JsonFieldType.STRING).description("음식점 이름"),
                                        fieldWithPath("orderHistories[].shopImg").type(JsonFieldType.STRING).description("음식점 아이콘 URI"),
                                        fieldWithPath("orderHistories[].menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                                        fieldWithPath("orderHistories[].menuCount").type(JsonFieldType.NUMBER).description("메뉴 개수"),
                                        fieldWithPath("orderHistories[].totalMenuCount").type(JsonFieldType.NUMBER).description("총 메뉴 개수"),
                                        fieldWithPath("lastId").type(JsonFieldType.NUMBER).description("마지막 주문 ID"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재여부")
                                )
                        )
                )
                ;

        verify(orderService).getOrderHistory(any(), any());
    }

    @DisplayName("상세주문내역 조회")
    @Test
    void getOrderDetails() throws Exception {

        OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                .orderId(1L)
                .orderNumber("10OCT0_2312")
                .status(Status.DONE)
                .orderType(OrderType.DELIVERY)
                .shopName("BHC 행당점")
                .shopId(1L)
                .address(new Address("14582", "다산로 4길 57", "장미아파트 8동"))
                .orderTime(LocalDateTime.now())
                .orderItems(
                        List.of(
                                OrderItem.builder()
                                        .id(1L)
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
                .requestMsg("요청사항 없음")
                .requestDoor(true)
                .requestSpoon(false)
                .orderType(OrderType.DELIVERY)
                .paymentType(PaymentType.CARD)
                .totalPrice(20000)
                .deliveryPrice(1000)
                .paymentPrice(21000)
                .build();

        given(orderService.getOrderDetail(any(), any())).willReturn(orderDetailResponse);

        mockMvc.perform(
                    get("/order/details")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", jwt)
                    .param("orderId", "1")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order/details",
                            requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                            ),
                            requestParameters(
                                parameterWithName("orderId").description("주문 ID")
                            ),
                            responseFields(
                                    fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                    fieldWithPath("status").type(JsonFieldType.STRING).description("주문상태"),
                                    fieldWithPath("orderType").type(JsonFieldType.STRING).description("주문방식"),
                                    fieldWithPath("shopName").type(JsonFieldType.STRING).description("음식점 이름"),
                                    fieldWithPath("shopId").type(JsonFieldType.NUMBER).description("음식점 ID"),
                                    fieldWithPath("orderNumber").type(JsonFieldType.STRING).description("주문번호"),
                                    fieldWithPath("orderTime").type(JsonFieldType.STRING).description("주문시간"),

                                    fieldWithPath("orderItems[].id").type(JsonFieldType.NUMBER).description("주문 아이템 ID"),
                                    fieldWithPath("orderItems[].createdAt").ignored(),
                                    fieldWithPath("orderItems[].updatedAt").ignored(),
                                    fieldWithPath("orderItems[].price").type(JsonFieldType.NUMBER).description("가격"),
                                    fieldWithPath("orderItems[].quantity").type(JsonFieldType.NUMBER).description("개수"),
                                    fieldWithPath("orderItems[].menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                                    //fieldWithPath("orderItems[].order").ignored(),

                                    fieldWithPath("orderItems[].orderItemOptions[].id").ignored(),
                                    fieldWithPath("orderItems[].orderItemOptions[].optionName").type(JsonFieldType.STRING).description("옵션 이름"),
                                    fieldWithPath("orderItems[].orderItemOptions[].price").type(JsonFieldType.NUMBER).description("가격"),
                                    //fieldWithPath("orderItems[].orderItemOptions[].orderItem").ignored(),

                                    fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("총 금액"),
                                    fieldWithPath("deliveryPrice").type(JsonFieldType.NUMBER).description("배달 금액"),
                                    fieldWithPath("paymentPrice").type(JsonFieldType.NUMBER).description("결제금액"),
                                    fieldWithPath("paymentType").type(JsonFieldType.STRING).description("결제방식"),
                                    fieldWithPath("address.zipcode").type(JsonFieldType.STRING).description("우편번호"),
                                    fieldWithPath("address.street").type(JsonFieldType.STRING).description("도로명 주소"),
                                    fieldWithPath("address.detail").type(JsonFieldType.STRING).description("상세주소"),
                                    fieldWithPath("requestMsg").type(JsonFieldType.STRING).description("요청사항"),
                                    fieldWithPath("requestDoor").type(JsonFieldType.BOOLEAN).description("문 앞에 두기 여부"),
                                    fieldWithPath("requestSpoon").type(JsonFieldType.BOOLEAN).description("수저여부")
                            )
                        )
                );

        verify(orderService).getOrderDetail(any(), any());
    }
}