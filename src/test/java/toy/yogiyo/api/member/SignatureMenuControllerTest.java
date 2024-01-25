package toy.yogiyo.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import toy.yogiyo.core.menu.service.SignatureMenuService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignatureMenuController.class)
@ExtendWith(RestDocumentationExtension.class)
class SignatureMenuControllerTest {

    @MockBean
    SignatureMenuService signatureMenuService;

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
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/member/signature-menu/shop/{shopId}", 1));

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
                .andDo(document("member/signature-menu/find-all",
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

}