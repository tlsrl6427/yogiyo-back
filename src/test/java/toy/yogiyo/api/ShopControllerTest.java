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
import org.springframework.web.filter.CharacterEncodingFilter;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.common.security.WithLoginOwner;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.shop.domain.*;
import toy.yogiyo.core.shop.dto.*;
import toy.yogiyo.core.shop.service.ShopService;
import toy.yogiyo.util.ConstrainedFields;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static toy.yogiyo.document.utils.DocumentLinkGenerator.DocUrl.DAYS;
import static toy.yogiyo.document.utils.DocumentLinkGenerator.generateLinkCode;

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
                .categories(Arrays.asList("치킨", "한식", "중국집"))
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
        ConstrainedFields fields = new ConstrainedFields(ShopRegisterRequest.class);
        result.andExpect(status().isCreated())
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
                                fields.withPath("name").type(JsonFieldType.STRING).description("가게명"),
                                fields.withPath("callNumber").type(JsonFieldType.STRING).description("전화번호"),
                                fields.withPath("address").type(JsonFieldType.STRING).description("주소"),
                                fields.withPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fields.withPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fields.withPath("categories").type(JsonFieldType.ARRAY).description("카테고리 Array")
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
        ShopNoticeResponse response = ShopNoticeResponse.builder()
                .title("공지 제목")
                .notice("공지 내용")
                .images(List.of("/images/image1.png", "/images/image2.png"))
                .build();
        when(shopService.getNotice(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/shop/{shopId}/notice", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("공지 제목"))
                .andExpect(jsonPath("$.notice").value("공지 내용"))
                .andExpect(jsonPath("$.images[0]").value("/images/image1.png"))
                .andExpect(jsonPath("$.images[1]").value("/images/image2.png"))
                .andDo(print())
                .andDo(document("shop/get-notice",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("사장님 공지 제목"),
                                fieldWithPath("notice").type(JsonFieldType.STRING).description("사장님 공지 내용"),
                                fieldWithPath("images").type(JsonFieldType.ARRAY).description("사장님 공지 사진 리스트")
                        )
                ));
    }

    @Test
    @DisplayName("영업 시간 조회")
    void getBusinessHours() throws Exception {
        // given
        ShopBusinessHourResponse response = ShopBusinessHourResponse.builder()
                .businessHours(List.of(
                        new ShopBusinessHourResponse.BusinessHoursDto(BusinessHours.builder()
                                .dayOfWeek(Days.EVERYDAY)
                                .isOpen(true)
                                .openTime(LocalTime.of(10, 0))
                                .closeTime(LocalTime.of(20, 0))
                                .build()
                        )
                ))
                .build();
        when(shopService.getBusinessHours(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/shop/{shopId}/business-hours", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.businessHours.length()").value(1))
                .andExpect(jsonPath("$.businessHours[0].dayOfWeek").value("EVERYDAY"))
                .andExpect(jsonPath("$.businessHours[0].openTime").value("10:00:00"))
                .andDo(print())
                .andDo(document("shop/get-business-hours",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("businessHours").type(JsonFieldType.ARRAY).description("영업 시간"),
                                fieldWithPath("businessHours[].dayOfWeek").type(JsonFieldType.STRING).description(generateLinkCode(DAYS)),
                                fieldWithPath("businessHours[].isOpen").type(JsonFieldType.BOOLEAN).description("영업일"),
                                fieldWithPath("businessHours[].openTime").type(JsonFieldType.STRING).description("오픈 시간"),
                                fieldWithPath("businessHours[].closeTime").type(JsonFieldType.STRING).description("마감 시간"),
                                fieldWithPath("businessHours[].breakTimeStart").type(JsonFieldType.STRING).description("휴게 시간 (시작)").optional(),
                                fieldWithPath("businessHours[].breakTimeEnd").type(JsonFieldType.STRING).description("휴게 시간 (종료)").optional()
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
    @DisplayName("휴무일 조회")
    void getCloseDays() throws Exception {
        // given
        when(shopService.getCloseDays(anyLong())).thenReturn(ShopCloseDayResponse.builder()
                .closeDays(List.of(
                        new ShopCloseDayResponse.CloseDayDto(CloseDay.builder().weekNumOfMonth(1).dayOfWeek(Days.MONDAY).build()),
                        new ShopCloseDayResponse.CloseDayDto(CloseDay.builder().weekNumOfMonth(3).dayOfWeek(Days.MONDAY).build())
                ))
                .build());

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/shop/{shopId}/close-day", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.closeDays[0].weekNumOfMonth").value(1))
                .andExpect(jsonPath("$.closeDays[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.closeDays[1].weekNumOfMonth").value(3))
                .andExpect(jsonPath("$.closeDays[1].dayOfWeek").value("MONDAY"))
                .andDo(print())
                .andDo(document("shop/get-close-day",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("closeDays").type(JsonFieldType.ARRAY).description("휴무일 리스트"),
                                fieldWithPath("closeDays[].weekNumOfMonth").type(JsonFieldType.NUMBER).description("(1~4)번째 주"),
                                fieldWithPath("closeDays[].dayOfWeek").type(JsonFieldType.STRING).description(generateLinkCode(DAYS))
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
        ConstrainedFields fields = new ConstrainedFields(ShopUpdateCallNumberRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("shop/update-call-number",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fields.withPath("callNumber").type(JsonFieldType.STRING).description("전화번호")
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
        ConstrainedFields fields = new ConstrainedFields(ShopNoticeUpdateRequest.class);
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/shop/{shopId}/notice/update", 1)
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(requestJson)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        result.andExpect(status().isNoContent())
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
                                fields.withPath("title").type(JsonFieldType.STRING).description("공지 제목"),
                                fields.withPath("notice").type(JsonFieldType.STRING).description("공지 내용")
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
                .businessHours(List.of(
                        new ShopBusinessHourUpdateRequest.BusinessHoursDto(
                                Days.EVERYDAY,
                                true,
                                LocalTime.of(10, 0),
                                LocalTime.of(20, 0),
                                null,
                                null)
                ))
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/shop/{shopId}/business-hours/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .characterEncoding("utf8"));

        // then
        ConstrainedFields fields = new ConstrainedFields(ShopBusinessHourUpdateRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("shop/update-business-hours",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fields.withPath("businessHours").type(JsonFieldType.ARRAY).description("영업 시간"),
                                fields.withPath("businessHours[].dayOfWeek").type(JsonFieldType.STRING).description(generateLinkCode(DAYS)),
                                fields.withPath("businessHours[].isOpen").type(JsonFieldType.BOOLEAN).description("영업일"),
                                fields.withPath("businessHours[].openTime").type(JsonFieldType.STRING).description("오픈 시간"),
                                fields.withPath("businessHours[].closeTime").type(JsonFieldType.STRING).description("마감 시간"),
                                fields.withPath("businessHours[].breakTimeStart").type(JsonFieldType.STRING).description("휴게 시간 (시작)").optional(),
                                fields.withPath("businessHours[].breakTimeEnd").type(JsonFieldType.STRING).description("휴게 시간 (종료)").optional()
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
        ConstrainedFields fields = new ConstrainedFields(DeliveryPriceUpdateRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("shop/update-delivery-price",
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
    @DisplayName("휴무일 수정")
    void updateCloseDays() throws Exception {
        // given
        doNothing().when(shopService).updateCloseDays(anyLong(), any(), any());
        ShopCloseDayUpdateRequest request = ShopCloseDayUpdateRequest.builder()
                .closeDays(List.of(
                        new ShopCloseDayUpdateRequest.CloseDayDto(1, Days.MONDAY),
                        new ShopCloseDayUpdateRequest.CloseDayDto(3, Days.MONDAY)
                ))
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/shop/{shopId}/close-day/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        ConstrainedFields fields = new ConstrainedFields(ShopCloseDayUpdateRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("shop/update-close-day",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fields.withPath("closeDays").type(JsonFieldType.ARRAY).description("휴무일 리스트"),
                                fields.withPath("closeDays[].weekNumOfMonth").type(JsonFieldType.NUMBER).description("(1~4)번째 주"),
                                fields.withPath("closeDays[].dayOfWeek").type(JsonFieldType.STRING).description(generateLinkCode(DAYS))
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
        result.andExpect(status().isNoContent())
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

    @DisplayName("상점 리스트 조회")
    @Test
    void getList() throws Exception {
        ShopScrollListRequest request = ShopScrollListRequest.builder()
                .category("치킨")
                .sortOption("CLOSEST")
                .deliveryPrice(3000)
                .leastOrderPrice(10000)
                .longitude(127.0215778)
                .latitude(37.5600233)
                .offset(0L)
                .limit(5L)
                .build();

        Scroll<ShopScrollResponse> response = new Scroll<>(List.of(
                ShopScrollResponse.builder()
                        .shopId(9036L)
                        .shopName("음식점 9036")
                        .totalScore(1.124858862726861)
                        .distance(7364.810136664925)
                        .deliveryTime(37)
                        .minDeliveryPrice(1500)
                        .maxDeliveryPrice(0)
                        .icon("/images/yogiyo-logo.jpg")
                        .build(),
                ShopScrollResponse.builder()
                        .shopId(6640L)
                        .shopName("음식점 6640")
                        .totalScore(3.5151195901468153)
                        .distance(7420.250353367057)
                        .deliveryTime(51)
                        .minDeliveryPrice(3000)
                        .maxDeliveryPrice(0)
                        .icon("/images/yogiyo-logo.jpg")
                        .build()
        ), 6L, true);

        given(shopService.getList(any())).willReturn(response);

        mockMvc.perform(get("/shop/list")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
//                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("category", "치킨")
                        .param("sortOption","CLOSEST")
                        .param("deliveryPrice","3000")
                        .param("leastOrderPrice","10000")
                        .param("longitude","127.0215778")
                        .param("latitude","37.5600233")
                        .param("offset","0")
                        .param("limit","5")
                        )
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andDo(document("shop/list",
                                requestParameters(
                                        parameterWithName("category").description("카테고리"),
                                        parameterWithName("sortOption").description("정렬 기준(CLOSEST, ORDER,  REVIEW, SCORE)"),
                                        parameterWithName("deliveryPrice").description("배달요금")
                                                .attributes(key("constraints").value("0 or 양수")),
                                        parameterWithName("leastOrderPrice").description("최소 주문 금액")
                                                .attributes(key("constraints").value("0 or 양수")),
                                        parameterWithName("longitude").description("경도")
                                                .attributes(key("constraints").value("Not Null")),
                                        parameterWithName("latitude").description("위도")
                                                .attributes(key("constraints").value("Not Null")),
                                        parameterWithName("offset").description("오프셋(시작 번호)"),
                                        parameterWithName("limit").description("개수")
                                ),
                                responseFields(
                                        fieldWithPath("content[].shopId").type(JsonFieldType.NUMBER).description("음식점 ID"),
                                        fieldWithPath("content[].shopName").type(JsonFieldType.STRING).description("음식점 이름"),
                                        fieldWithPath("content[].totalScore").type(JsonFieldType.NUMBER).description("총 점수"),
                                        fieldWithPath("content[].distance").type(JsonFieldType.NUMBER).description("거리"),
                                        fieldWithPath("content[].deliveryTime").type(JsonFieldType.NUMBER).description("배달시간"),
                                        fieldWithPath("content[].minDeliveryPrice").type(JsonFieldType.NUMBER).description("최소 배달금액"),
                                        fieldWithPath("content[].maxDeliveryPrice").type(JsonFieldType.NUMBER).description("최대 배달금액"),
                                        fieldWithPath("content[].icon").type(JsonFieldType.STRING).description("아이콘 URL"),
                                        fieldWithPath("nextOffset").type(JsonFieldType.NUMBER).description("다음 시작 오프셋"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재여부")
                                )
                        ));


        verify(shopService).getList(any());
    }

    private Shop givenShop() {
        Shop shop = Shop.builder()
                .id(1L)
                .name("롯데리아")
                .icon("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .banner("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .ownerNotice("사장님 공지")
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