package toy.yogiyo.api.member;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.common.security.WithLoginMember;
import toy.yogiyo.core.shop.domain.Days;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.member.ShopInfoResponse;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListRequest;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListResponse;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollResponse;
import toy.yogiyo.core.shop.repository.ShopRepository;
import toy.yogiyo.core.shop.service.ShopService;
import toy.yogiyo.document.utils.DocumentLinkGenerator;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
import static toy.yogiyo.document.utils.DocumentLinkGenerator.DocUrl.*;
import static toy.yogiyo.document.utils.DocumentLinkGenerator.generateLinkCode;

@WebMvcTest(ShopController.class)
@ExtendWith(RestDocumentationExtension.class)
class ShopControllerTest {

    @MockBean
    ShopService shopService;

    @MockBean
    ShopRepository shopRepository;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

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

    @DisplayName("상점 리스트 조회")
    @Test
    void getList() throws Exception {
        ShopScrollListRequest request = ShopScrollListRequest.builder()
                .category(ShopScrollListRequest.ShopCategory.Chicken)
                .sortOption(ShopScrollListRequest.SortOption.ORDER)
                .deliveryPrice(3000)
                .leastOrderPrice(10000)
                .longitude(127.0215778)
                .latitude(37.5600233)
                .code("1111011500")
                .cursor(BigDecimal.valueOf(500))
                .subCursor(100000L)
                .size(2L)
                .build();

        ShopScrollListResponse response = ShopScrollListResponse.builder()
                .content(List.of(
                        ShopScrollResponse.builder()
                                .shopId(9036L)
                                .shopName("음식점 9036")
                                .orderNum(323L)
                                .reviewNum(93L)
                                .deliveryTime(30)
                                .minDeliveryPrice(3000)
                                .maxDeliveryPrice(10000)
                                .totalScore(BigDecimal.valueOf(1.124858862726861))
                                .distance(7364.810136664925)
                                .icon("/images/yogiyo-logo.jpg")
                                .build(),
                        ShopScrollResponse.builder()
                                .shopId(6640L)
                                .shopName("음식점 6640")
                                .orderNum(118L)
                                .reviewNum(6L)
                                .deliveryTime(40)
                                .minDeliveryPrice(2000)
                                .maxDeliveryPrice(15000)
                                .totalScore(BigDecimal.valueOf(3.5151195901468153))
                                .distance(7420.250353367057)
                                .icon("/images/yogiyo-logo.jpg")
                                .build()
                ))
                .nextCursor(BigDecimal.valueOf(323))
                .nextSubCursor(9036L)
                .hasNext(false)
                .build();

        given(shopService.getList(any())).willReturn(response);

        mockMvc.perform(get("/member/shop/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("category", "치킨")
                        .param("sortOption","ORDER")
                        .param("deliveryPrice","3000")
                        .param("leastOrderPrice","10000")
                        .param("longitude","127.0215778")
                        .param("latitude","37.5600233")
                        .param("code", "1111011500")
                        .param("cursor","500")
                        .param("subCursor","100000")
                        .param("size","2")
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
                                        parameterWithName("code").description("법정동코드")
                                                .attributes(key("constraints").value("Not Null")),
                                        parameterWithName("cursor").description("메인 커서(정렬 기준 되는 컬럼값)"),
                                        parameterWithName("subCursor").description("서브 커서(마지막 음식점 ID)"),
                                        parameterWithName("size").description("리스트 조회할 개수")
                                ),
                                responseFields(
                                        fieldWithPath("content[].shopId").type(JsonFieldType.NUMBER).description("음식점 ID"),
                                        fieldWithPath("content[].shopName").type(JsonFieldType.STRING).description("음식점 이름"),
                                        fieldWithPath("content[].orderNum").type(JsonFieldType.NUMBER).description("주문수"),
                                        fieldWithPath("content[].reviewNum").type(JsonFieldType.NUMBER).description("리뷰수"),
                                        fieldWithPath("content[].deliveryTime").type(JsonFieldType.NUMBER).description("배달시간"),
                                        fieldWithPath("content[].minDeliveryPrice").type(JsonFieldType.NUMBER).description("최소 배달금액"),
                                        fieldWithPath("content[].maxDeliveryPrice").type(JsonFieldType.NUMBER).description("최대 배달금액"),
                                        fieldWithPath("content[].totalScore").type(JsonFieldType.NUMBER).description("총 점수"),
                                        fieldWithPath("content[].distance").type(JsonFieldType.NUMBER).description("거리"),
                                        fieldWithPath("content[].icon").type(JsonFieldType.STRING).description("아이콘 URL"),
                                        fieldWithPath("nextCursor").type(JsonFieldType.NUMBER).description("다음 시작 커서값"),
                                        fieldWithPath("nextSubCursor").type(JsonFieldType.NUMBER).description("다음 시작 음식점 ID"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재여부")
                                )
                        ));


        verify(shopService).getList(any());
    }

    @Test
    @DisplayName("가게 상세 정보 조회")
    @WithLoginMember
    void details() throws Exception {
        // given
        given(shopRepository.details(anyLong(), any())).willReturn(
                ShopDetailsResponse.builder()
                        .id(1L)
                        .name("음식점 1")
                        .reviewNum(438L)
                        .likeNum(314L)
                        .totalScore(BigDecimal.valueOf(3.34))
                        .banner("/images/banner.jpg")
                        .noticeTitle("공지 사항")
                        .distance(7029.0)
                        .minOrderPrice(10000)
                        .minDeliveryPrice(3000)
                        .isLike(true)
                        .build()
        );

        // when
        ResultActions result = mockMvc.perform(get("/member/shop/details")
                .param("shopId", "1")
                .param("code", "4373031030")
                .param("latitude", "37.512464")
                .param("longitude", "127.102543"));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("shop/details",
                        requestParameters(
                                parameterWithName("shopId").description("가게 ID")
                                        .attributes(key("constraints").value("Not Null")),
                                parameterWithName("code").description("법정동 코드")
                                        .attributes(key("constraints").value("Not Null")),
                                parameterWithName("latitude").description("위도")
                                        .attributes(key("constraints").value("Not Null")),
                                parameterWithName("longitude").description("경도")
                                        .attributes(key("constraints").value("Not Null"))

                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("가게명"),
                                fieldWithPath("reviewNum").type(JsonFieldType.NUMBER).description("리뷰 개수"),
                                fieldWithPath("likeNum").type(JsonFieldType.NUMBER).description("찜 개수"),
                                fieldWithPath("totalScore").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("banner").type(JsonFieldType.STRING).description("가게 배너 이미지"),
                                fieldWithPath("noticeTitle").type(JsonFieldType.STRING).description("공지 사항 제목"),
                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("거리"),
                                fieldWithPath("minOrderPrice").type(JsonFieldType.NUMBER).description("최소 주문 금액"),
                                fieldWithPath("minDeliveryPrice").type(JsonFieldType.NUMBER).description("최소 배달 금액"),
                                fieldWithPath("isLike").type(JsonFieldType.BOOLEAN).description("찜 여부")
                        )
                ));
    }

    @Test
    @DisplayName("가게 원산지 정보 조회")
    void info() throws Exception {
        // given
        given(shopRepository.info(anyLong(), anyString())).willReturn(
                ShopInfoResponse.builder()
                        .id(1L)
                        .name("음식점 1")
                        .noticeTitle("공지사항")
                        .ownerNotice("공지사항 입니다.")
                        .noticeImages(List.of("/images/image1.png", "/images/image2.png"))
                        .callNumber("010-1234-5678")
                        .address("서울특별시 송파구 올림픽로 300")
                        .deliveryPriceInfos(List.of(
                                ShopInfoResponse.DeliveryPriceInfoDto.builder()
                                        .id(4L)
                                        .deliveryPrice(5000)
                                        .orderPrice(26000)
                                        .build(),
                                ShopInfoResponse.DeliveryPriceInfoDto.builder()
                                        .id(5L)
                                        .deliveryPrice(4000)
                                        .orderPrice(31000)
                                        .build(),
                                ShopInfoResponse.DeliveryPriceInfoDto.builder()
                                        .id(6L)
                                        .deliveryPrice(2000)
                                        .orderPrice(15000)
                                        .build()
                        ))
                        .businessHours(List.of(
                                ShopInfoResponse.BusinessHoursDto.builder()
                                        .id(1L)
                                        .breakTimeStart(null)
                                        .breakTimeEnd(null)
                                        .openTime(LocalTime.of(8, 0))
                                        .closeTime(LocalTime.of(18, 0))
                                        .dayOfWeek(Days.SUNDAY)
                                        .isOpen(true)
                                        .build(),
                                ShopInfoResponse.BusinessHoursDto.builder()
                                        .id(2L)
                                        .breakTimeStart(LocalTime.of(14, 0))
                                        .breakTimeEnd(LocalTime.of(16, 0))
                                        .openTime(LocalTime.of(8, 0))
                                        .closeTime(LocalTime.of(19, 0))
                                        .dayOfWeek(Days.MONDAY)
                                        .isOpen(false)
                                        .build(),
                                ShopInfoResponse.BusinessHoursDto.builder()
                                        .id(3L)
                                        .breakTimeStart(LocalTime.of(14, 0))
                                        .breakTimeEnd(LocalTime.of(16, 0))
                                        .openTime(LocalTime.of(8, 0))
                                        .closeTime(LocalTime.of(19, 0))
                                        .dayOfWeek(Days.TUESDAY)
                                        .isOpen(true)
                                        .build(),
                                ShopInfoResponse.BusinessHoursDto.builder()
                                        .id(4L)
                                        .breakTimeStart(null)
                                        .breakTimeEnd(null)
                                        .openTime(LocalTime.of(7, 0))
                                        .closeTime(LocalTime.of(19, 0))
                                        .dayOfWeek(Days.WEDNESDAY)
                                        .isOpen(true)
                                        .build(),
                                ShopInfoResponse.BusinessHoursDto.builder()
                                        .id(5L)
                                        .breakTimeStart(LocalTime.of(14, 0))
                                        .breakTimeEnd(LocalTime.of(16, 0))
                                        .openTime(LocalTime.of(8, 0))
                                        .closeTime(LocalTime.of(0, 0))
                                        .dayOfWeek(Days.THURSDAY)
                                        .isOpen(false)
                                        .build(),
                                ShopInfoResponse.BusinessHoursDto.builder()
                                        .id(6L)
                                        .breakTimeStart(null)
                                        .breakTimeEnd(null)
                                        .openTime(LocalTime.of(8, 0))
                                        .closeTime(LocalTime.of(22, 0))
                                        .dayOfWeek(Days.FRIDAY)
                                        .isOpen(false)
                                        .build(),
                                ShopInfoResponse.BusinessHoursDto.builder()
                                        .id(7L)
                                        .breakTimeStart(LocalTime.of(14, 0))
                                        .breakTimeEnd(LocalTime.of(16, 0))
                                        .openTime(LocalTime.of(7, 0))
                                        .closeTime(LocalTime.of(19, 0))
                                        .dayOfWeek(Days.SATURDAY)
                                        .isOpen(true)
                                        .build()
                        ))
                        .build()
        );

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/member/shop/{shopId}/info", 1)
                .param("code", "4783025326"));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("member/shop/info",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestParameters(
                                parameterWithName("code").description("법정동 코드")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("가게명"),
                                fieldWithPath("noticeTitle").type(JsonFieldType.STRING).description("공지 사항 제목"),
                                fieldWithPath("ownerNotice").type(JsonFieldType.STRING).description("공지 사항 내용"),
                                fieldWithPath("noticeImages[]").type(JsonFieldType.ARRAY).description("공지 사항 이미지 리스트"),
                                fieldWithPath("callNumber").type(JsonFieldType.STRING).description("가게 전화 번호"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("가게 주소"),
                                fieldWithPath("deliveryPriceInfos[].id").type(JsonFieldType.NUMBER).description("배달 요금 ID"),
                                fieldWithPath("deliveryPriceInfos[].deliveryPrice").type(JsonFieldType.NUMBER).description("배달 요금"),
                                fieldWithPath("deliveryPriceInfos[].orderPrice").type(JsonFieldType.NUMBER).description("주문 금액"),
                                fieldWithPath("businessHours[].id").type(JsonFieldType.NUMBER).description("영업 시간 ID"),
                                fieldWithPath("businessHours[].breakTimeStart").type(JsonFieldType.STRING).description("휴게 시간 시작").optional(),
                                fieldWithPath("businessHours[].breakTimeEnd").type(JsonFieldType.STRING).description("휴게 시간 끝").optional(),
                                fieldWithPath("businessHours[].openTime").type(JsonFieldType.STRING).description("영업 시작 시간"),
                                fieldWithPath("businessHours[].closeTime").type(JsonFieldType.STRING).description("영업 종료 시간"),
                                fieldWithPath("businessHours[].dayOfWeek").type(JsonFieldType.STRING).description(generateLinkCode(DAYS)),
                                fieldWithPath("businessHours[].open").type(JsonFieldType.BOOLEAN).description("영업 유무")
                                )
                ));
    }

}