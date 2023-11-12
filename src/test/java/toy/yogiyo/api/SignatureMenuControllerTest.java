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
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.menu.dto.SignatureMenuUpdatePositionRequest;
import toy.yogiyo.core.menu.dto.SignatureMenuSetRequest;
import toy.yogiyo.core.menu.service.SignatureMenuService;
import toy.yogiyo.util.ConstrainedFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SignatureMenuController.class)
@ExtendWith(RestDocumentationExtension.class)
class SignatureMenuControllerTest {

    @MockBean
    SignatureMenuService signatureMenuService;

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
    @DisplayName("대표 메뉴 설정")
    void setSignatureMenu() throws Exception {
        // given
        given(signatureMenuService.deleteAll(anyLong())).willReturn(5);
        given(signatureMenuService.create(any())).willReturn(1L);

        SignatureMenuSetRequest request = SignatureMenuSetRequest.builder()
                .shopId(1L)
                .menuIds(Arrays.asList(1L, 2L, 3L, 4L, 5L))
                .build();

        // when
        ResultActions result = mockMvc.perform(put("/signature-menu/set")
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        ConstrainedFields fields = new ConstrainedFields(SignatureMenuSetRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("signature-menu/set",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        requestFields(
                                fields.withPath("shopId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fields.withPath("menuIds").type(JsonFieldType.ARRAY).description("메뉴 ID Array, Array 순서대로 position 지정")
                        )
                ));
    }

    @Test
    @DisplayName("대표 메뉴 전체 조회")
    void getSignatureMenus() throws Exception {
        // given
        List<SignatureMenu> signatureMenus = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            signatureMenus.add(SignatureMenu.builder().id(i + 1L).position(i + 1)
                    .menu(Menu.builder().id(i + 1L).name("메뉴" + i).content("메뉴" + i + " 설명").picture("image.png").price(10000).build())
                    .build());
        }
        given(signatureMenuService.getAll(anyLong())).willReturn(signatureMenus);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/signature-menu/shop/{shopId}", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.signatureMenus").isArray())
                .andExpect(jsonPath("$.signatureMenus.length()").value(5))
                .andExpect(jsonPath("$.signatureMenus[0].id").value(1))
                .andExpect(jsonPath("$.signatureMenus[1].id").value(2))
                .andExpect(jsonPath("$.signatureMenus[2].id").value(3))
                .andExpect(jsonPath("$.signatureMenus[3].id").value(4))
                .andExpect(jsonPath("$.signatureMenus[4].id").value(5))
                .andDo(print())
                .andDo(document("signature-menu/find-all",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("signatureMenus").type(JsonFieldType.ARRAY).description("대표 메뉴 Array"),
                                fieldWithPath("signatureMenus[].id").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                                fieldWithPath("signatureMenus[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("signatureMenus[].content").type(JsonFieldType.STRING).description("메뉴 설명"),
                                fieldWithPath("signatureMenus[].picture").type(JsonFieldType.STRING).description("메뉴 사진"),
                                fieldWithPath("signatureMenus[].price").type(JsonFieldType.NUMBER).description("메뉴 가격")
                        )
                ));
    }

    @Test
    @DisplayName("대표 메뉴 삭제")
    void delete() throws Exception {
        // given
        doNothing().when(signatureMenuService).delete(anyLong());

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/signature-menu/delete/{menuId}", 1));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("signature-menu/delete-one",
                        pathParameters(
                                parameterWithName("menuId").description("메뉴 ID")
                        )
                ));
    }

    @Test
    @DisplayName("대표 메뉴 정렬 순서 변경")
    void changeOrder() throws Exception {
        // given
        doNothing().when(signatureMenuService).updateMenuPosition(anyLong(), anyList());
        SignatureMenuUpdatePositionRequest request = SignatureMenuUpdatePositionRequest.builder()
                .menuIds(Arrays.asList(1L, 2L, 3L, 4L, 5L))
                .build();

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.put("/signature-menu/{shopId}/change-order", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        ConstrainedFields fields = new ConstrainedFields(SignatureMenuUpdatePositionRequest.class);
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("signature-menu/change-order",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                        ),
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        requestFields(
                                fields.withPath("menuIds").type(JsonFieldType.ARRAY).description("메뉴 ID Array, 순서대로 정렬됨")
                        )
                ));
    }

}