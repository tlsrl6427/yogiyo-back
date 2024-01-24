package toy.yogiyo.api.owner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.common.security.WithLoginOwner;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPlace;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPriceInfo;
import toy.yogiyo.core.deliveryplace.service.DeliveryPlaceService;
import toy.yogiyo.core.deliveryplace.dto.DeliveryPlaceAddRequest;
import toy.yogiyo.core.deliveryplace.dto.DeliveryPriceDto;
import toy.yogiyo.core.deliveryplace.dto.DeliveryPriceUpdateRequest;
import toy.yogiyo.util.ConstrainedFields;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryPlaceController.class)
@ExtendWith(RestDocumentationExtension.class)
class DeliveryPlaceControllerTest {

    @MockBean
    DeliveryPlaceService deliveryPlaceService;

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

    @Test
    @DisplayName("배달 가능 지역 추가")
    void add() throws Exception {
        // given
        doNothing().when(deliveryPlaceService).add(anyLong(), any());
        DeliveryPlaceAddRequest request = DeliveryPlaceAddRequest.builder()
                .code("1171010200")
                .name("서울특별시 송파구 신천동")
                .deliveryTime(60)
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/owner/delivery-place/shop/{shopId}/add", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)));

        // then
        ConstrainedFields fields = new ConstrainedFields(DeliveryPlaceAddRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delivery-place/add",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fields.withPath("code").type(JsonFieldType.STRING).description("법정동 코드"),
                                fields.withPath("name").type(JsonFieldType.STRING).description("법정동명"),
                                fields.withPath("deliveryTime").type(JsonFieldType.NUMBER).description("예상 배달 시간")
                        )
                ));
    }

    @Test
    @DisplayName("배달 가능 지역 삭제")
    void delete() throws Exception {
        // given
        doNothing().when(deliveryPlaceService).delete(anyLong());

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/owner/delivery-place/{deliveryPlaceId}/delete", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delivery-place/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("deliveryPlaceId").description("배달 가능 지역 ID")
                        )
                ));
    }


    @Test
    @DisplayName("배달 요금 수정")
    @WithLoginOwner
    void updateDeliveryPrice() throws Exception {
        // given
        doNothing().when(deliveryPlaceService).updateDeliveryPrice(anyLong(), any());
        DeliveryPriceUpdateRequest request = DeliveryPriceUpdateRequest.builder()
                .deliveryPrices(List.of(
                        new DeliveryPriceDto(15000, 4500),
                        new DeliveryPriceDto(25000, 3500),
                        new DeliveryPriceDto(35000, 2500)))
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/owner/delivery-place/{deliveryPlaceId}/delivery-price/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        ConstrainedFields fields = new ConstrainedFields(DeliveryPriceUpdateRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delivery-place/update-delivery-price",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("deliveryPlaceId").description("배달 가능 지역 ID")
                        ),
                        requestFields(
                                fields.withPath("deliveryPrices").type(JsonFieldType.ARRAY).description("배달 요금 리스트"),
                                fields.withPath("deliveryPrices[].orderPrice").type(JsonFieldType.NUMBER).description("주문 금액"),
                                fields.withPath("deliveryPrices[].deliveryPrice").type(JsonFieldType.NUMBER).description("배달 금액")
                        )
                ));
    }

    @Test
    @DisplayName("배달 요금 일괄 수정")
    @WithLoginOwner
    void updateDeliveryPriceByShop() throws Exception {
        // given
        doNothing().when(deliveryPlaceService).updateDeliveryPriceByShop(anyLong(), any());
        DeliveryPriceUpdateRequest request = DeliveryPriceUpdateRequest.builder()
                .deliveryPrices(List.of(
                        new DeliveryPriceDto(15000, 4500),
                        new DeliveryPriceDto(25000, 3500),
                        new DeliveryPriceDto(35000, 2500)))
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/owner/delivery-place/shop/{shopId}/delivery-price/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        ConstrainedFields fields = new ConstrainedFields(DeliveryPriceUpdateRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delivery-place/update-all-delivery-price",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fields.withPath("deliveryPrices").type(JsonFieldType.ARRAY).description("배달 요금 리스트"),
                                fields.withPath("deliveryPrices[].orderPrice").type(JsonFieldType.NUMBER).description("주문 금액"),
                                fields.withPath("deliveryPrices[].deliveryPrice").type(JsonFieldType.NUMBER).description("배달 금액")
                        )
                ));
    }

    @Test
    @DisplayName("배달 요금 조회")
    void getDeliveryPrice() throws Exception {
        // given
        when(deliveryPlaceService.get(anyLong())).thenReturn(DeliveryPlace.builder()
                .deliveryPriceInfos(List.of(
                        new DeliveryPriceInfo(10000, 5000),
                        new DeliveryPriceInfo(20000, 4000),
                        new DeliveryPriceInfo(30000, 3000)
                ))
                .build());

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/owner/delivery-place/{deliveryPlaceId}/delivery-price", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryPrices").isArray())
                .andExpect(jsonPath("$.deliveryPrices.length()").value(3))
                .andDo(print())
                .andDo(document("delivery-place/get-delivery-price",
                        pathParameters(
                                parameterWithName("deliveryPlaceId").description("배달 가능 지역 ID")
                        ),
                        responseFields(
                                fieldWithPath("deliveryPrices").type(JsonFieldType.ARRAY).description("배달 요금 리스트"),
                                fieldWithPath("deliveryPrices[].orderPrice").type(JsonFieldType.NUMBER).description("주문 금액"),
                                fieldWithPath("deliveryPrices[].deliveryPrice").type(JsonFieldType.NUMBER).description("배달 요금")
                        )
                ));
    }
}