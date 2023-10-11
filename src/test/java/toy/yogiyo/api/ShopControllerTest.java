package toy.yogiyo.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.common.security.WithLoginOwner;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.dto.*;
import toy.yogiyo.core.shop.service.ShopService;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopController.class)
@ExtendWith(RestDocumentationExtension.class)
class ShopControllerTest {

    @MockBean
    ShopService shopService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    final String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInByb3ZpZGVyVHlwZSI6IkRFRkFVTFQiLCJleHAiOjE2OTQ5NjY4Mjh9.Ls1wnxU41I99ijXRyKfkYI2w3kd-Q_qA2QgCLgpDTKk";

    @BeforeEach
    void beforeEach(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("가게 입점")
    @WithLoginOwner
    void register() throws Exception {
        // given
        ShopRegisterRequest registerRequest = ShopRegisterRequest.builder()
                .name("롯데리아")
                .callNumber("010-1234-5678")
                .address("서울 강남구 영동대로 513")
                .latitude(36.674648)
                .longitude(127.448544)
                .categoryIds(Arrays.asList(1L, 2L, 3L))
                .build();

        MockMultipartFile requestJson = new MockMultipartFile(
                "shopData",
                "jsonData",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(registerRequest).getBytes());
        MockMultipartFile icon = givenIcon();
        MockMultipartFile banner = givenBanner();
        when(shopService.register(any(), any(), any(), any())).thenReturn(1L);

        // when
        ResultActions result = mockMvc.perform(multipart("/shop/register")
                .file(icon)
                .file(banner)
                .file(requestJson)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andDo(print())
                .andDo(document("shop/register",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        requestParts(
                                partWithName("icon").description("아이콘"),
                                partWithName("banner").description("배너 이미지"),
                                partWithName("shopData").description("가게 정보")
                        ),
                        requestPartFields("shopData",
                                fieldWithPath("name").type(JsonFieldType.STRING).description("가게명"),
                                fieldWithPath("callNumber").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("가게 ID")
                        )
                ));
    }


    @Test
    @DisplayName("가게 정보 조회")
    void getInfo() throws Exception {
        // given
        ShopInfoResponse response = ShopInfoResponse.from(givenShop());
        when(shopService.getInfo(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/shop/{shopId}/info", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.callNumber").value(response.getCallNumber()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories.length()").value(response.getCategories().size()))
                .andDo(print())
                .andDo(document("shop/get-info",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("가게명"),
                                fieldWithPath("callNumber").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("categories").type(JsonFieldType.ARRAY).description("카테고리명 리스트")
                        )
                ));
    }

    @Test
    @DisplayName("사장님 공지 조회")
    void getNotice() throws Exception {
        // given
        ShopNoticeResponse response = ShopNoticeResponse.from(givenShop());
        when(shopService.getNotice(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/shop/{shopId}/notice", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.notice").value(response.getNotice()))
                .andDo(print())
                .andDo(document("shop/get-notice",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("notice").type(JsonFieldType.STRING).description("사장님 공지")
                        )
                ));
    }

    @Test
    @DisplayName("영업 시간 조회")
    void getBusinessHours() throws Exception {
        // given
        ShopBusinessHourResponse response = ShopBusinessHourResponse.from(givenShop());
        when(shopService.getBusinessHours(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/shop/{shopId}/business-hours", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.businessHours").value(response.getBusinessHours()))
                .andDo(print())
                .andDo(document("shop/get-business-hours",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("businessHours").type(JsonFieldType.STRING).description("영업 시간")
                        )
                ));
    }

    @Test
    @DisplayName("배달 요금 조회")
    void getDeliveryPrice() throws Exception {
        // given
        ShopDeliveryPriceResponse response = ShopDeliveryPriceResponse.from(givenShop());
        when(shopService.getDeliveryPrice(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/shop/{shopId}/delivery-price", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryPrices").isArray())
                .andExpect(jsonPath("$.deliveryPrices.length()").value(response.getDeliveryPrices().size()))
                .andDo(print())
                .andDo(document("shop/get-delivery-price",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("deliveryPrices").type(JsonFieldType.ARRAY).description("배달 요금 리스트"),
                                fieldWithPath("deliveryPrices[].orderPrice").type(JsonFieldType.NUMBER).description("주문 금액"),
                                fieldWithPath("deliveryPrices[].deliveryPrice").type(JsonFieldType.NUMBER).description("배달 요금")
                        )
                ));
    }

    @Test
    @DisplayName("가게 전화번호 수정")
    @WithLoginOwner
    void updateCallNumber() throws Exception {
        // given
        ShopUpdateCallNumberRequest updateRequest = ShopUpdateCallNumberRequest.builder()
                .callNumber("010-1234-5678")
                .build();

        doNothing().when(shopService).updateCallNumber(anyLong(), any(), any());

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/shop/{shopId}/call-number/update", 1L)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print())
                .andDo(document("shop/update-call-number",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fieldWithPath("callNumber").type(JsonFieldType.STRING).description("전화번호")
                        )
                ));
    }

    @Test
    @DisplayName("사장님 공지 수정")
    @WithLoginOwner
    void updateNotice() throws Exception {
        // given
        doNothing().when(shopService).updateNotice(anyLong(), any(), any(), anyList());
        ShopNoticeUpdateRequest request = ShopNoticeUpdateRequest.builder()
                .title("공지 제목")
                .notice("사장님 공지")
                .build();
        MockMultipartFile image1 = new MockMultipartFile("images", "image1.png", MediaType.IMAGE_PNG_VALUE, "<<image.png>>".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "image2.png", MediaType.IMAGE_PNG_VALUE, "<<image.png>>".getBytes());
        MockMultipartFile image3 = new MockMultipartFile("images", "image3.png", MediaType.IMAGE_PNG_VALUE, "<<image.png>>".getBytes());
        MockMultipartFile requestJson = new MockMultipartFile(
                "noticeData",
                "jsonData",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes());


        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/shop/{shopId}/notice/update", 1)
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(requestJson)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print())
                .andDo(document("shop/update-notice",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestParts(
                                partWithName("images").description("첨부 사진"),
                                partWithName("noticeData").description("공지 Json")
                        ),
                        requestPartFields("noticeData",
                                fieldWithPath("title").type(JsonFieldType.STRING).description("공지 제목"),
                                fieldWithPath("notice").type(JsonFieldType.STRING).description("공지 내용")
                        )
                ));
    }

    @Test
    @DisplayName("영업 시간 수정")
    @WithLoginOwner
    void updateBusinessHours() throws Exception {
        // given
        doNothing().when(shopService).updateBusinessHours(anyLong(), any(), any());
        ShopBusinessHourUpdateRequest request = ShopBusinessHourUpdateRequest.builder()
                .businessHours("오전 10시 ~ 오후 10시")
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/shop/{shopId}/business-hours/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print())
                .andDo(document("shop/update-business-hours",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fieldWithPath("businessHours").type(JsonFieldType.STRING).description("영업 시간")
                        )
                ));
    }

    @Test
    @DisplayName("배달 요금 수정")
    @WithLoginOwner
    void updateDeliveryPrice() throws Exception {
        // given
        doNothing().when(shopService).updateDeliveryPrice(anyLong(), any(), any());
        DeliveryPriceUpdateRequest request = DeliveryPriceUpdateRequest.builder()
                .deliveryPrices(Arrays.asList(
                        new DeliveryPriceDto(15000, 4500),
                        new DeliveryPriceDto(25000, 3500),
                        new DeliveryPriceDto(35000, 2500)))
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/shop/{shopId}/delivery-price/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print())
                .andDo(document("shop/update-delivery-price",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fieldWithPath("deliveryPrices").type(JsonFieldType.ARRAY).description("배달 요금 리스트"),
                                fieldWithPath("deliveryPrices[].orderPrice").type(JsonFieldType.NUMBER).description("주문 금액"),
                                fieldWithPath("deliveryPrices[].deliveryPrice").type(JsonFieldType.NUMBER).description("배달 금액")
                        )
                ));
    }

    @Test
    @DisplayName("가게 삭제")
    @WithLoginOwner
    void deleteShop() throws Exception {
        // given
        doNothing().when(shopService).delete(anyLong(), any());

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/shop/{shopId}/delete", 1L)
                .header(HttpHeaders.AUTHORIZATION, jwt));

        // then
        verify(shopService).delete(anyLong(), any());
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print())
                .andDo(document("shop/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        )
                ));
    }

    private Shop givenShop() {
        Shop shop = Shop.builder()
                .id(1L)
                .name("롯데리아")
                .icon("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .banner("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .ownerNotice("사장님 공지")
                .businessHours("오전 10시 ~ 오후 10시")
                .callNumber("010-1234-5678")
                .address("서울 강남구 영동대로 513")
                .deliveryTime(30)
                .categoryShop(Arrays.asList(
                        CategoryShop.builder().category(Category.builder().name("카테고리1").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리2").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리3").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리4").build()).build()
                ))
                .build();

        shop.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(10000, 5000),
                new DeliveryPriceInfo(20000, 4000),
                new DeliveryPriceInfo(30000, 3000)));


        return shop;
    }

    private MockMultipartFile givenIcon() throws IOException {
        return new MockMultipartFile("icon", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
    }

    private MockMultipartFile givenBanner() throws IOException {
        return new MockMultipartFile("banner", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
    }

}