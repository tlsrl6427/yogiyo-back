package toy.yogiyo.api;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;
import toy.yogiyo.core.Member.dto.MemberJoinRequest;
import toy.yogiyo.core.Member.dto.MemberJoinResponse;
import toy.yogiyo.core.Member.dto.MemberMypageResponse;
import toy.yogiyo.core.Member.dto.MemberUpdateRequest;
import toy.yogiyo.core.Member.service.MemberService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@ExtendWith(RestDocumentationExtension.class)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    ObjectMapper objectMapper;
    MockMvc mockMvc;
    Member member;

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

        member = Member.builder()
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .build();
    }

    @DisplayName("멤버 회원가입 API")
    @Test
    void join() throws Exception{
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .providerType(ProviderType.DEFAULT)
                .build();

        MemberJoinResponse memberJoinResponse = MemberJoinResponse.builder()
                .id(1L)
                .build();

        given(memberService.join(any())).willReturn(memberJoinResponse);

        mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/member/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(memberJoinRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("member/join",
                            requestFields(
                                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                    fieldWithPath("providerType").type(JsonFieldType.STRING).description("공급자 타입")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("멤버 ID")
                            )
                        )
                );

        verify(memberService).join(any());
    }

    @DisplayName("멤버 마이페이지 조회 API")
    @Test
    void showMypage() throws Exception{
        MemberMypageResponse memberMypageResponse = MemberMypageResponse.builder()
                .nickname("test")
                .email("test@gmail.com")
                .build();

        given(memberService.findOne(any())).willReturn(memberMypageResponse);

        mockMvc.perform(
                    get("/member/mypage")
                    .header("Authorization", jwt)
                )
                .andExpect(status().isOk())
                .andDo(print())
                        .andDo(
                                document("/member/mypage",
                                    requestHeaders(
                                            headerWithName("Authorization").description("Access Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                    )
                                )
                        );

        verify(memberService).findOne(any());
    }

    @DisplayName("멤버정보 업데이트 API")
    @Test
    void update() throws Exception{
        MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
                .nickname("test2")
                .build();

        mockMvc.perform(
                    patch("/member/update")
                    .header("Authorization", jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(memberUpdateRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("member/update",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                requestFields(
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                                )
                        )
                );
    }

    @DisplayName("멤버삭제 API")
    @Test
    void delete_member() throws Exception{

        mockMvc.perform(
                    delete("/member/delete")
                    .header("Authorization", jwt)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("member/delete",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                )
                        )
                );
    }
}