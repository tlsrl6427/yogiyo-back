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
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;

import java.util.Arrays;
import java.util.List;

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

@WebMvcTest(MenuGroupController.class)
@ExtendWith(RestDocumentationExtension.class)
class MenuGroupControllerTest {

    @MockBean
    MenuGroupRepository menuGroupRepository;

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
    @DisplayName("메뉴 그룹 전체 조회")
    void getMenuGroups() throws Exception {
        // given
        List<Menu> menus1 = Arrays.asList(
                Menu.builder().id(1L).name("메뉴 1").content("메뉴 1 설명").picture("image.png").price(10000).reviewNum(20).position(1).build(),
                Menu.builder().id(2L).name("메뉴 2").content("메뉴 2 설명").picture("image.png").price(10000).reviewNum(0).position(2).build(),
                Menu.builder().id(3L).name("메뉴 3").content("메뉴 3 설명").picture("image.png").price(10000).reviewNum(100).position(3).build()
        );
        List<Menu> menus2 = Arrays.asList(
                Menu.builder().id(4L).name("메뉴 4").content("메뉴 4 설명").picture("image.png").price(10000).reviewNum(5).position(1).build(),
                Menu.builder().id(5L).name("메뉴 5").content("메뉴 5 설명").picture("image.png").price(10000).reviewNum(10).position(2).build(),
                Menu.builder().id(6L).name("메뉴 6").content("메뉴 6 설명").picture("image.png").price(10000).reviewNum(3).position(3).build()
        );

        List<MenuGroup> menuGroups = Arrays.asList(
                MenuGroup.builder().id(1L).name("메뉴 그룹1").content("메뉴 그룹1 설명").menus(menus1).build(),
                MenuGroup.builder().id(2L).name("메뉴 그룹2").content("메뉴 그룹2 설명").menus(menus2).build()
        );
        given(menuGroupRepository.findAllSellableWithMenuByShopId(anyLong())).willReturn(menuGroups);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/member/menu-group/shop/{shopId}", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.menuGroups").isArray())
                .andExpect(jsonPath("$.menuGroups.length()").value(2))
                .andExpect(jsonPath("$.menuGroups[0].id").value(1))
                .andExpect(jsonPath("$.menuGroups[1].id").value(2))
                .andExpect(jsonPath("$.menuGroups[0].menus[0].id").value(1))
                .andExpect(jsonPath("$.menuGroups[0].menus[1].id").value(2))
                .andExpect(jsonPath("$.menuGroups[0].menus[2].id").value(3))
                .andDo(print())
                .andDo(document("member/menu-group/find-all",
                        pathParameters(
                                parameterWithName("shopId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("menuGroups").type(JsonFieldType.ARRAY).description("메뉴 그룹 Array"),
                                fieldWithPath("menuGroups[].id").type(JsonFieldType.NUMBER).description("메뉴 그룹 ID"),
                                fieldWithPath("menuGroups[].name").type(JsonFieldType.STRING).description("메뉴 그룹 이름"),
                                fieldWithPath("menuGroups[].content").type(JsonFieldType.STRING).description("메뉴 그룹 설명"),
                                fieldWithPath("menuGroups[].menus").type(JsonFieldType.ARRAY).description("메뉴 Array"),
                                fieldWithPath("menuGroups[].menus[].id").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                                fieldWithPath("menuGroups[].menus[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("menuGroups[].menus[].content").type(JsonFieldType.STRING).description("메뉴 설명"),
                                fieldWithPath("menuGroups[].menus[].price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
                                fieldWithPath("menuGroups[].menus[].reviewNum").type(JsonFieldType.NUMBER).description("리뷰 개수"),
                                fieldWithPath("menuGroups[].menus[].picture").type(JsonFieldType.STRING).description("메뉴 사진")
                        )
                ));
    }

}