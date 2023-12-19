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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.common.login.service.LoginService;
import toy.yogiyo.common.security.jwt.JwtProvider;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.domain.ProviderType;
import toy.yogiyo.core.owner.domain.Owner;


import javax.servlet.http.Cookie;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@ExtendWith(RestDocumentationExtension.class)
class LoginControllerTest {

    @MockBean
    LoginService loginService;
    @MockBean
    JwtProvider jwtProvider;

    ObjectMapper objectMapper;
    MockMvc mockMvc;

    final String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInByb3ZpZGVyVHlwZSI6IkRFRkFVTFQiLCJleHAiOjE2OTQ5NjY4Mjh9.Ls1wnxU41I99ijXRyKfkYI2w3kd-Q_qA2QgCLgpDTKk";

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

    @DisplayName("멤버 기본 로그인")
    @Test
    void member_default_login() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("1234")
                .providerType(ProviderType.DEFAULT)
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .userId(1L)
                .email("test@gmail.com")
                .build();

        given(loginService.memberLogin(any())).willReturn(loginResponse);
        given(jwtProvider.createToken(any(), any(), any())).willReturn(jwt);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/memberLogin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(loginRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("default/login",
                            requestFields(
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                    fieldWithPath("authCode").ignored(),
                                    fieldWithPath("providerType").type(JsonFieldType.STRING).description("DEFAULT")
                            ),
                            responseFields(
                                    fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일")
                            ),
                            responseHeaders(
                                    headerWithName("Set-Cookie").description("Access Token")
                            )
                        )
                );

        verify(loginService).memberLogin(any());
        verify(jwtProvider).createToken(any(), any(), any());
    }

    @DisplayName("멤버 소셜 로그인")
    @Test
    void member_social_login() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .authCode("4/0Adeu5BXHXMmmdhnrPdOZSVv0ZNbehLLQNAB1D8kUv10gAsOjCv2-5wQRHe2yRDXhmJlg1g")
                .providerType(ProviderType.GOOGLE)
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .userId(1L)
                .email("test@gmail.com")
                .build();



        given(loginService.memberLogin(any())).willReturn(loginResponse);
        given(jwtProvider.createToken(any(), any(), any())).willReturn(jwt);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/memberLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("social/login",
                                requestFields(
                                        fieldWithPath("email").ignored(),
                                        fieldWithPath("password").ignored(),
                                        fieldWithPath("authCode").type(JsonFieldType.STRING).description("auth_code"),
                                        fieldWithPath("providerType").type(JsonFieldType.STRING).description("공급자 타입(GOOGLE, KAKAO, NAVER)")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일")
                                ),
                                responseHeaders(
                                        headerWithName("Set-Cookie").description("Access Token")
                                )
                        )
                );

        verify(loginService).memberLogin(any());
        verify(jwtProvider).createToken(any(), any(), any());
    }

    @DisplayName("멤버 로그아웃")
    @Test
    @WithMockUser()
    void member_default_logout() throws Exception {
        Member member = Member.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("1234")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/memberLogout/{memberId}", 1L)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("default/logout",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").description("멤버 ID")
                                ),
                                responseHeaders(
                                        headerWithName("Set-Cookie").description("쿠키 삭제")
                                )
                        )
                );
    }

    @DisplayName("점주 기본 로그인")
    @Test
    void owner_default_login() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("1234")
                .providerType(ProviderType.DEFAULT)
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .userId(1L)
                .email("test@gmail.com")
                .build();

        given(loginService.ownerLogin(any())).willReturn(loginResponse);
        given(jwtProvider.createToken(any(), any(), any())).willReturn(jwt);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/ownerLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("owner/default/login",
                                requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                        fieldWithPath("authCode").ignored(),
                                        fieldWithPath("providerType").type(JsonFieldType.STRING).description("DEFAULT")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일")
                                ),
                                responseHeaders(
                                        headerWithName("Set-Cookie").description("Access Token")
                                )
                        )
                );

        verify(loginService).ownerLogin(any());
        verify(jwtProvider).createToken(any(), any(), any());
    }

    @DisplayName("점주 소셜 로그인")
    @Test
    void owner_social_login() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .authCode("4/0Adeu5BXHXMmmdhnrPdOZSVv0ZNbehLLQNAB1D8kUv10gAsOjCv2-5wQRHe2yRDXhmJlg1g")
                .providerType(ProviderType.GOOGLE)
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .userId(1L)
                .email("test@gmail.com")
                .build();

        given(loginService.ownerLogin(any())).willReturn(loginResponse);
        given(jwtProvider.createToken(any(), any(), any())).willReturn(jwt);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/ownerLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("owner/social/login",
                                requestFields(
                                        fieldWithPath("email").ignored(),
                                        fieldWithPath("password").ignored(),
                                        fieldWithPath("authCode").type(JsonFieldType.STRING).description("auth_code"),
                                        fieldWithPath("providerType").type(JsonFieldType.STRING).description("공급자 타입(GOOGLE, KAKAO, NAVER)")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일")
                                ),
                                responseHeaders(
                                        headerWithName("Set-Cookie").description("Access Token")
                                )
                        )
                );

        verify(loginService).ownerLogin(any());
        verify(jwtProvider).createToken(any(), any(), any());
    }

    @DisplayName("점주 로그아웃")
    @Test
    void owner_default_logout() throws Exception {
        Owner owner = Owner.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("1234")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(owner, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/ownerLogout/{ownerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("owner/default/logout",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("ownerId").description("멤버 ID")
                                ),
                                responseHeaders(
                                        headerWithName("Set-Cookie").description("쿠키 삭제")
                                )
                        )
                );
    }
}