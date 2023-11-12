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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.domain.ReviewImage;
import toy.yogiyo.core.review.dto.ReplyRequest;
import toy.yogiyo.core.review.dto.ReviewQueryCondition;
import toy.yogiyo.core.review.repository.ReviewQueryRepository;
import toy.yogiyo.core.review.service.ReviewManagementService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewManagementController.class)
@ExtendWith(RestDocumentationExtension.class)
class ReviewManagementControllerTest {

    @MockBean
    ReviewQueryRepository reviewQueryRepository;

    @MockBean
    ReviewManagementService reviewManagementService;

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
    @DisplayName("리뷰 확인")
    void getShopReviews() throws Exception {
        // given
        List<Review> reviews = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Review review = Review.builder()
                    .id((long) i)
                    .tasteScore(5.0f)
                    .deliveryScore(5.0f)
                    .quantityScore(5.0f)
                    .totalScore(5.0f)
                    .content("양도 많고 감자도 잘 튀겨졌어요~~")
                    .member(Member.builder().nickname("abcde").build())
                    .reviewImages(List.of(
                            new ReviewImage(1L, "image.png", null),
                            new ReviewImage(2L, "image.png", null),
                            new ReviewImage(3L, "image.png", null)
                    ))
                    .build();
            review.setCreatedAt(LocalDateTime.of(2023, 10, 21, 0, 0, 0));
            reviews.add(review);
        }
        given(reviewQueryRepository.shopReviewScroll(anyLong(), any()))
                .willReturn(new Scroll<>(reviews, 0, 10, true));

        ReviewQueryCondition condition = ReviewQueryCondition.builder()
                .sort(ReviewQueryCondition.Sort.LATEST)
                .startDate(LocalDate.of(2023, 10, 20))
                .endDate(LocalDate.of(2023, 10, 23))
                .size(10)
                .number(0)
                .status(ReviewQueryCondition.Status.ALL)
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/management/review/shop/{shopId}", 1)
                .param("sort", condition.getSort().name())
                .param("startDate", condition.getStartDate().toString())
                .param("endDate", condition.getEndDate().toString())
                .param("size", String.valueOf(condition.getSize()))
                .param("number", String.valueOf(condition.getNumber()))
                .param("status", condition.getStatus().name()));
//        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[9].id").value(10))
                .andDo(print())
                .andDo(document("management/review/shop",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestParameters(
                                parameterWithName("sort").description("정렬 기준"),
                                parameterWithName("startDate").description("조회 시작 날짜"),
                                parameterWithName("endDate").description("조회 끝 날짜"),
                                parameterWithName("size").description("콘텐츠 개수"),
                                parameterWithName("number").description("스크롤 번호"),
                                parameterWithName("status").description("답변 상태")
                        ),
                        responseFields(
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("스크롤 번호 (0부터 시작)"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("스크롤 아이템 개수"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 스크롤 유무"),
                                fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("content[].tasteScore").type(JsonFieldType.NUMBER).description("맛 점수"),
                                fieldWithPath("content[].quantityScore").type(JsonFieldType.NUMBER).description("양 점수"),
                                fieldWithPath("content[].deliveryScore").type(JsonFieldType.NUMBER).description("배달 점수"),
                                fieldWithPath("content[].totalScore").type(JsonFieldType.NUMBER).description("총 점수"),
                                fieldWithPath("content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("content[].ownerReply").type(JsonFieldType.STRING).description("사장님 답변 내용").optional(),
                                fieldWithPath("content[].memberName").type(JsonFieldType.STRING).description("리뷰 작성자 명"),
                                fieldWithPath("content[].date").type(JsonFieldType.STRING).description("리뷰 작성 날짜"),
                                fieldWithPath("content[].reviewImages").type(JsonFieldType.ARRAY).description("리뷰 사진 Array")
                        )
                ));
    }

    @Test
    @DisplayName("답변")
    void reply() throws Exception {
        // given
        doNothing().when(reviewManagementService).reply(anyLong(), anyString());
        ReplyRequest request = ReplyRequest.builder()
                .reply("답변")
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/management/review/{reviewId}/reply", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("management/review/reply",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        requestFields(
                                fieldWithPath("reply").type(JsonFieldType.STRING).description("답변 내용")
                        )
                ));
    }

    @Test
    @DisplayName("답변 삭제")
    void deleteReply() throws Exception {
        // given
        doNothing().when(reviewManagementService).deleteReply(anyLong());

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/management/review/{reviewId}/reply", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("management/review/reply-delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        )
                ));
    }


}