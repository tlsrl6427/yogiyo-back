package toy.yogiyo.api.member;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.domain.ReviewImage;
import toy.yogiyo.core.review.dto.*;
import toy.yogiyo.core.review.repository.ReviewQueryRepository;
import toy.yogiyo.core.review.service.ReviewService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.yogiyo.document.utils.DocumentLinkGenerator.DocUrl.REVIEW_SORT;
import static toy.yogiyo.document.utils.DocumentLinkGenerator.generateLinkCode;

@WebMvcTest(ReviewController.class)
@ExtendWith(RestDocumentationExtension.class)
class ReviewControllerTest {

    @MockBean
    ReviewService reviewService;
    @MockBean
    ReviewQueryRepository reviewQueryRepository;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    final String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInByb3ZpZGVyVHlwZSI6IkRFRkFVTFQiLCJleHAiOjE2OTQ5NjY4Mjh9.Ls1wnxU41I99ijXRyKfkYI2w3kd-Q_qA2QgCLgpDTKk";

    @BeforeEach
    void beforeEach(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("리뷰 생성")
    @Test
    void write() throws Exception {
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
                .orderId(1L)
                .tasteScore(BigDecimal.valueOf(3.5))
                .quantityScore(BigDecimal.valueOf(3.0))
                .deliveryScore(BigDecimal.valueOf(5.0))
                .content("맛있어요~")
                .shopId(1L)
                .shopName("BHC 행당점")
                .build();

        doNothing().when(reviewService).create(any(), any());

        mockMvc.perform(
                        post("/member/review/write")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", jwt)
                                .content(objectMapper.writeValueAsString(reviewCreateRequest))
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(
                        document("review/write",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                requestFields(
                                        fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 ID")
                                                .attributes(key("constraints").value("Not Null")),
                                        fieldWithPath("tasteScore").type(JsonFieldType.NUMBER).description("맛 점수"),
                                        fieldWithPath("quantityScore").type(JsonFieldType.NUMBER).description("양 점수"),
                                        fieldWithPath("deliveryScore").type(JsonFieldType.NUMBER).description("배달 점수"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                                                .attributes(key("constraints").value("Not Empty")),
                                        fieldWithPath("shopId").type(JsonFieldType.NUMBER).description("음식점 ID")
                                                .attributes(key("constraints").value("Not Null")),
                                        fieldWithPath("shopName").type(JsonFieldType.STRING).description("음식점 이름")
                                )
                        )
                );

        verify(reviewService).create(any(), any());
    }

    @DisplayName("멤버 리뷰 조회")
    @Test
    void getMemberReview() throws Exception {

        MemberReviewScrollResponse memberReviewScrollResponse = MemberReviewScrollResponse.builder()
                .reviews(
                        List.of(
                                Review.builder()
                                        .id(1L)
                                        .tasteScore(BigDecimal.valueOf(3.5))
                                        .quantityScore(BigDecimal.valueOf(3.0))
                                        .deliveryScore(BigDecimal.valueOf(5.0))
                                        .totalScore(BigDecimal.valueOf(3.8))
                                        .ownerReply("사장님 댓글입니다")
                                        .ownerReplyCreatedAt(LocalDateTime.of(2023, 10, 23, 0, 0, 0))
                                        .content("맛있어요~")
                                        .shopId(1L)
                                        .shopName("BHC 행당점")
                                        .reviewImages(
                                                List.of(
                                                        new ReviewImage(1L, "img.jpg", null)
                                                )
                                        )
                                        .build()
                        )
                )
                .nextLastId(1L)
                .hasNext(false)
                .build();

        given(reviewService.getMemberReviews(any(), any())).willReturn(memberReviewScrollResponse);

        mockMvc.perform(
                        get("/member/review/memberReview")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", jwt)
                                .param("lastId", "6")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document(
                        "review/memberReview",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        requestParameters(
                                parameterWithName("lastId").description("마지막 리뷰 ID")
                        ),
                        responseFields(

                                fieldWithPath("reviews[].createdAt").ignored(),
                                fieldWithPath("reviews[].updatedAt").ignored(),
                                fieldWithPath("reviews[].member").ignored(),
                                fieldWithPath("reviews[].order").ignored(),

                                fieldWithPath("reviews[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("reviews[].tasteScore").type(JsonFieldType.NUMBER).description("맛 점수"),
                                fieldWithPath("reviews[].quantityScore").type(JsonFieldType.NUMBER).description("양 점수"),
                                fieldWithPath("reviews[].deliveryScore").type(JsonFieldType.NUMBER).description("배달 점수"),
                                fieldWithPath("reviews[].totalScore").type(JsonFieldType.NUMBER).description("총 점수"),
                                fieldWithPath("reviews[].content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("reviews[].ownerReply").type(JsonFieldType.STRING).description("사장님 댓글"),
                                fieldWithPath("reviews[].ownerReplyCreatedAt").type(JsonFieldType.STRING).description("사장님 댓글 작성 시간"),
                                fieldWithPath("reviews[].shopId").type(JsonFieldType.NUMBER).description("음식점 ID"),
                                fieldWithPath("reviews[].shopName").type(JsonFieldType.STRING).description("음식점 이름"),
                                fieldWithPath("reviews[].reviewImages[].id").type(JsonFieldType.NUMBER).description(""),
                                fieldWithPath("reviews[].reviewImages[].imgSrc").type(JsonFieldType.STRING).description("이미지 경로"),
                                fieldWithPath("nextLastId").type(JsonFieldType.NUMBER).description("마지막 리뷰 ID"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 스크롤 존재 여부")
                        )
                ));

        verify(reviewService).getMemberReviews(any(), any());
    }


    @Test
    @DisplayName("리뷰 목록 조회")
    void getShopReviews() throws Exception {
        // given
        List<ReviewResponse> response = new ArrayList<>();
        for (int i = 11; i <= 20; i++) {
            ReviewResponse review = ReviewResponse.builder()
                    .id((long) i)
                    .tasteScore(BigDecimal.valueOf(5.0))
                    .deliveryScore(BigDecimal.valueOf(5.0))
                    .quantityScore(BigDecimal.valueOf(5.0))
                    .totalScore(BigDecimal.valueOf(5.0))
                    .content("양도 많고 감자도 잘 튀겨졌어요~~")
                    .memberName("사용자 1")
                    .createdAt(LocalDateTime.of(2023, 10, 21, 0, 0, 0))
                    .reviewImages(List.of("images/image1.png", "images/image2.png", "images/image3.png"))
                    .menus(List.of(
                            new ReviewResponse.MenuDto("메뉴 1", 1, 10000),
                            new ReviewResponse.MenuDto("메뉴 2", 1, 12000),
                            new ReviewResponse.MenuDto("메뉴 3", 2, 10000)
                    ))
                    .build();
            response.add(review);
        }
        given(reviewQueryRepository.shopReviewScroll(anyLong(), any()))
                .willReturn(new Scroll<>(response, 20, null, true));

        ReviewQueryCondition condition = ReviewQueryCondition.builder()
                .sort(ReviewQueryCondition.Sort.LATEST)
                .cursor(11)
                .limit(10)
                .build();

        // when
        ResultActions result = mockMvc.perform(get("/member/review/shop-review")
                .param("shopId", "1")
                .param("sort", condition.getSort().name())
                .param("cursor", condition.getCursor().toString())
                .param("limit", String.valueOf(condition.getLimit())));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("review/shopReview",
                        requestParameters(
                                parameterWithName("shopId").description("가게 ID"),
                                parameterWithName("sort").description(generateLinkCode(REVIEW_SORT)),
                                parameterWithName("cursor").description("다음 스크롤 커서").optional(),
                                parameterWithName("limit").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER).description("다음 스크롤 커서"),
                                fieldWithPath("nextSubCursor").type(JsonFieldType.NUMBER).description("다음 스크롤 서브 커서").optional(),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 스크롤 유무"),
                                fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("content[].tasteScore").type(JsonFieldType.NUMBER).description("맛 점수"),
                                fieldWithPath("content[].quantityScore").type(JsonFieldType.NUMBER).description("양 점수"),
                                fieldWithPath("content[].deliveryScore").type(JsonFieldType.NUMBER).description("배달 점수"),
                                fieldWithPath("content[].totalScore").type(JsonFieldType.NUMBER).description("총 점수"),
                                fieldWithPath("content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("content[].ownerReply").type(JsonFieldType.STRING).description("사장님 답변 내용").optional(),
                                fieldWithPath("content[].memberName").type(JsonFieldType.STRING).description("리뷰 작성자 명"),
                                fieldWithPath("content[].createdAt").type(JsonFieldType.STRING).description("리뷰 작성 날짜"),
                                fieldWithPath("content[].reviewImages").type(JsonFieldType.ARRAY).description("리뷰 사진 Array"),
                                fieldWithPath("content[].menus[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("content[].menus[].quantity").type(JsonFieldType.NUMBER).description("메뉴 개수"),
                                fieldWithPath("content[].menus[].price").type(JsonFieldType.NUMBER).description("메뉴 가격")
                        )
                ));
    }

    @Test
    @DisplayName("리뷰 요약")
    void getReviewSUmmary() throws Exception {
        // given
        given(reviewQueryRepository.findReviewSummary(anyLong())).willReturn(
                ReviewGetSummaryResponse.builder()
                        .totalCount(3L)
                        .totalOwnerReplyCount(2L)
                        .avgTotalScore(2.4)
                        .avgTasteScore(2.1)
                        .avgQuantityScore(2.3)
                        .avgDeliveryScore(2.8)
                        .build()
        );

        // when
        ResultActions result = mockMvc.perform(get("/member/review/shop-review-summary")
                .param("shopId", "1"));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("review/shopReviewSummary",
                        requestParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("총 리뷰 수"),
                                fieldWithPath("totalOwnerReplyCount").type(JsonFieldType.NUMBER).description("답변 리뷰 수"),
                                fieldWithPath("totalNoReplyCount").type(JsonFieldType.NUMBER).description("미답변 리뷰 수"),
                                fieldWithPath("avgTotalScore").type(JsonFieldType.NUMBER).description("총 점수"),
                                fieldWithPath("avgTasteScore").type(JsonFieldType.NUMBER).description("맛 점수"),
                                fieldWithPath("avgQuantityScore").type(JsonFieldType.NUMBER).description("양 점수"),
                                fieldWithPath("avgDeliveryScore").type(JsonFieldType.NUMBER).description("배달 점수")
                        )
                ));
    }
}