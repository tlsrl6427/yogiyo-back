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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.core.member.domain.ProviderType;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.dto.OwnerJoinRequest;
import toy.yogiyo.core.owner.dto.OwnerJoinResponse;
import toy.yogiyo.core.owner.dto.OwnerMypageResponse;
import toy.yogiyo.core.owner.dto.OwnerUpdateRequest;
import toy.yogiyo.core.owner.service.OwnerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerController.class)
@ExtendWith(RestDocumentationExtension.class)
class OwnerControllerTest {

    @MockBean
    OwnerService ownerService;

    MockMvc mockMvc;
    ObjectMapper objectMapper;
    Owner owner;

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

        owner = Owner.builder()
                .id(1L)
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .build();
    }

    @DisplayName("점주 회원가입 API")
    @Test
    void join() throws Exception {
        OwnerJoinRequest ownerJoinRequest = OwnerJoinRequest.builder()
                .nickname("test")
                .email("test@gmail.com")
                .password("12345678")
                .providerType(ProviderType.DEFAULT)
                .build();

        OwnerJoinResponse ownerJoinResponse = OwnerJoinResponse.builder()
                .id(1L)
                .build();

        given(ownerService.join(any())).willReturn(ownerJoinResponse);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/owner/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(ownerJoinRequest))
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(
                        document("owner/join",
                                requestFields(
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                                                .attributes(key("constraints").value("2~16자")),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                                .attributes(key("constraints").value("이메일 형식")),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                                .attributes(key("constraints").value("8자 이상")),
                                        fieldWithPath("providerType").type(JsonFieldType.STRING).description("공급자 타입")
                                ),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("점주 ID")
                                )
                        )
                );

        verify(ownerService).join(any());
    }

    @DisplayName("점주 마이페이지 조회 API")
    @Test
    void showMypage() throws Exception {
        OwnerMypageResponse ownerMypageResponse = OwnerMypageResponse.builder()
                .nickname("test")
                .email("test@gmail.com")
                .build();

        given(ownerService.getMypage(any())).willReturn(ownerMypageResponse);

        mockMvc.perform(
                        get("/owner/mypage")
                                .header("Authorization", jwt)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("owner/mypage",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                responseFields(
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                )
                        )
                );

        verify(ownerService).getMypage(any());
    }

    @DisplayName("점주정보 업데이트 API")
    @Test
    void update() throws Exception {
        OwnerUpdateRequest ownerUpdateRequest = OwnerUpdateRequest.builder()
                .nickname("test2")
                .build();

        mockMvc.perform(
                        patch("/owner/update")
                                .header("Authorization", jwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(ownerUpdateRequest))
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        document("owner/update",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                ),
                                requestFields(
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                                )
                        )
                );
    }

    @DisplayName("점주삭제 API")
    @Test
    void delete_owner() throws Exception {
        mockMvc.perform(
                        delete("/owner/delete")
                                .header("Authorization", jwt)
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        document("owner/delete",
                                requestHeaders(
                                        headerWithName("Authorization").description("Access Token")
                                )
                        )
                );
    }
}