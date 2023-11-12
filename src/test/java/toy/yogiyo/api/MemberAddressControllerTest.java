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
import toy.yogiyo.core.address.domain.Address;
import toy.yogiyo.core.address.domain.AddressType;
import toy.yogiyo.core.address.domain.MemberAddress;
import toy.yogiyo.core.address.dto.AddressRegisterRequest;
import toy.yogiyo.core.address.dto.MemberAddressResponse;
import toy.yogiyo.core.address.service.MemberAddressService;
import toy.yogiyo.core.member.domain.Member;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberAddressController.class)
@ExtendWith(RestDocumentationExtension.class)
class MemberAddressControllerTest {

    @MockBean
    MemberAddressService memberAddressService;

    MockMvc mockMvc;
    ObjectMapper objectMapper;
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
                .id(1L)
                .build();
    }

    @DisplayName("주소 등록")
    @Test
    void register() throws Exception {
        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .nickname("내 집")
                .addressType(AddressType.HOME)
                .address(new Address("02833", "공릉로 232", "대성빌라 504호"))
                .latitude(34.2323494)
                .longitude(43.5549921)
                .build();

        doNothing().when(memberAddressService).register(any(), any());

        mockMvc.perform(post("/address/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("address/register",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("주소 별칭")
                                        .attributes(key("constraints").value("Not Blank")),
                                fieldWithPath("addressType").type(JsonFieldType.STRING).description("주소 타입(HOME, COMPANY, ELSE)"),
                                fieldWithPath("address.zipcode").type(JsonFieldType.STRING).description("우편번호"),
                                fieldWithPath("address.street").type(JsonFieldType.STRING).description("도로명 주소"),
                                fieldWithPath("address.detail").type(JsonFieldType.STRING).description("상세주소"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도")
                        )
                        )
                );

        verify(memberAddressService).register(any(), any());
    }

    @DisplayName("계정 귀속 주소목록 조회")
    @Test
    void getAddresses() throws Exception {
        MemberAddressResponse response = MemberAddressResponse.builder()
                .memberAddresses(
                        List.of(
                                MemberAddress.builder()
                                        .id(1L)
                                        .nickname("우리집")
                                        .addressType(AddressType.HOME)
                                        .address(new Address("02833", "공릉로 232", "대성빌라 504호"))
                                        .latitude(34.2323494)
                                        .longitude(43.5549921)
                                        .build(),
                                MemberAddress.builder()
                                        .id(2L)
                                        .nickname("회사")
                                        .addressType(AddressType.COMPANY)
                                        .address(new Address("23452", "양재대로 9길", "롯데빌딩 603호"))
                                        .latitude(24.11121)
                                        .longitude(84.123882)
                                        .build()
                        )
                )
                .build();

        given(memberAddressService.getAddresses(any())).willReturn(response);

        mockMvc.perform(get("/address/view")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("address/view",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        responseFields(
                                fieldWithPath("memberAddresses[].id").type(JsonFieldType.NUMBER).description("주소 ID"),
                                fieldWithPath("memberAddresses[].nickname").type(JsonFieldType.STRING).description("주소 별칭"),
                                fieldWithPath("memberAddresses[].addressType").type(JsonFieldType.STRING).description("주소 타입(HOME, COMPANY, ELSE)"),
                                fieldWithPath("memberAddresses[].address.zipcode").type(JsonFieldType.STRING).description("우편번호"),
                                fieldWithPath("memberAddresses[].address.street").type(JsonFieldType.STRING).description("도로명 주소"),
                                fieldWithPath("memberAddresses[].address.detail").type(JsonFieldType.STRING).description("상세주소"),
                                fieldWithPath("memberAddresses[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("memberAddresses[].longitude").type(JsonFieldType.NUMBER).description("경도")
                        )
                        ));

        verify(memberAddressService).getAddresses(any());
    }

    @DisplayName("주소 삭제")
    @Test
    void delete_address() throws Exception {

        doNothing().when(memberAddressService).delete(any(), any());

        mockMvc.perform(delete("/address/{memberAddressId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("address",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("memberAddressId").description("주소 ID")
                        )
                ));

        verify(memberAddressService).delete(any(), any());
    }
}