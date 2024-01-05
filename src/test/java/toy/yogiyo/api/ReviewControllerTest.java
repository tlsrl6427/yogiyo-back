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
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.domain.ReviewImage;
import toy.yogiyo.core.review.dto.MemberReviewScrollResponse;
import toy.yogiyo.core.review.dto.ReviewCreateRequest;
import toy.yogiyo.core.review.service.ReviewService;

import java.math.BigDecimal;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@ExtendWith(RestDocumentationExtension.class)
class ReviewControllerTest {

    @MockBean
    ReviewService reviewService;

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
                    post("/review/write")
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
                get("/review/memberReview")
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
    void getShopReview() {
    }
}