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
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.core.like.dto.LikeResponse;
import toy.yogiyo.core.like.dto.LikeScrollRequest;
import toy.yogiyo.core.like.service.LikeService;
import toy.yogiyo.core.shop.domain.Shop;

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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@ExtendWith(RestDocumentationExtension.class)
class LikeControllerTest {

    @MockBean
    LikeService likeService;

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

    @DisplayName("찜 기능-toggle")
    @Test
    void toggleLike() throws Exception {
        doNothing().when(likeService).toggleLike(any(), any());

        mockMvc.perform(post("/like/{shopId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwt)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("like/",
                            requestHeaders(
                                    headerWithName("Authorization").description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("shopId").description("음식점 ID")
                            )
                        )
                );
        verify(likeService).toggleLike(any(), any());
    }

    @DisplayName("찜 목록 조회-scroll")
    @Test
    void getLikes() throws Exception {
        LikeScrollRequest request = new LikeScrollRequest(0L, 5L);

        List<LikeResponse> likeResponseList = List.of(
                LikeResponse.builder()
                        .shopId(6L)
                        .shopName("BHC 행당점")
                        .shopImg("image1.jpg")
                        .score("4.7")
                        .build(),
                LikeResponse.builder()
                        .shopId(3L)
                        .shopName("맥도날드")
                        .shopImg("image2.jpg")
                        .score("3.6")
                        .build()

        );
        Scroll<LikeResponse> likeResponses = new Scroll<>(likeResponseList, 3L, false);


        given(likeService.getLikes(any(), any())).willReturn(likeResponses);

        mockMvc.perform(get("/like/scroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("offset", "0")
                        .param("limit", "5")
                        .header("Authorization", jwt)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("like/scroll",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("offset").description("스크롤 시작 ID"),
                                        parameterWithName("limit").description("스크롤 개수")
                                ),
                                responseFields(
                                        fieldWithPath("content[].shopId").type(JsonFieldType.NUMBER).description("음식점 ID"),
                                        fieldWithPath("content[].shopName").type(JsonFieldType.STRING).description("음식점 이름"),
                                        fieldWithPath("content[].shopImg").type(JsonFieldType.STRING).description("음식점 아이콘 이미지 URL"),
                                        fieldWithPath("content[].score").type(JsonFieldType.STRING).description("총 별점"),
                                        fieldWithPath("nextOffset").type(JsonFieldType.NUMBER).description("마지막 스크롤 ID"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재여부")
                                )
                        )
                );

        verify(likeService).getLikes(any(), any());
    }
}