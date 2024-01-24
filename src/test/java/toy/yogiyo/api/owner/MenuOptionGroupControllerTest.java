package toy.yogiyo.api.owner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.domain.OptionGroupLinkMenu;
import toy.yogiyo.core.menuoption.domain.OptionType;
import toy.yogiyo.core.menuoption.dto.*;
import toy.yogiyo.core.menuoption.service.MenuOptionGroupService;
import toy.yogiyo.core.menuoption.service.MenuOptionService;
import toy.yogiyo.util.ConstrainedFields;

import java.util.Arrays;
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
import static toy.yogiyo.document.utils.DocumentLinkGenerator.DocUrl.OPTION_TYPE;
import static toy.yogiyo.document.utils.DocumentLinkGenerator.generateLinkCode;

@WebMvcTest(MenuOptionGroupController.class)
@ExtendWith(RestDocumentationExtension.class)
class MenuOptionGroupControllerTest {

    @MockBean
    MenuOptionGroupService menuOptionGroupService;

    @MockBean
    MenuOptionService menuOptionService;

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

    @Nested
    @DisplayName("옵션 그룹")
    class OptionGroupTest {

        @Test
        @DisplayName("옵션 그룹 추가")
        void add() throws Exception {
            // given
            given(menuOptionGroupService.create(any())).willReturn(1L);

            MenuOptionGroupCreateRequest request = MenuOptionGroupCreateRequest.builder()
                    .name("옵션 그룹")
                    .optionType(OptionType.OPTIONAL)
                    .count(4)
                    .isPossibleCount(false)
                    .options(Arrays.asList(
                            MenuOptionGroupCreateRequest.OptionDto.builder().content("옵션1").price(1000).build(),
                            MenuOptionGroupCreateRequest.OptionDto.builder().content("옵션2").price(1000).build(),
                            MenuOptionGroupCreateRequest.OptionDto.builder().content("옵션3").price(1000).build(),
                            MenuOptionGroupCreateRequest.OptionDto.builder().content("옵션4").price(1000).build()
                    ))
                    .build();

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/owner/menu-option-group/shop/{shopId}/add", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            ConstrainedFields fields = new ConstrainedFields(MenuOptionGroupCreateRequest.class);
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.menuOptionGroupId").value(1))
                    .andDo(print())
                    .andDo(document("menu-option-group/add-group",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("shopId").description("가게 ID")
                            ),
                            requestFields(
                                    fields.withPath("name").type(JsonFieldType.STRING).description("옵션그룹명"),
                                    fields.withPath("optionType").type(JsonFieldType.STRING).description(generateLinkCode(OPTION_TYPE)),
                                    fields.withPath("count").type(JsonFieldType.NUMBER).description("옵션 선택 가능 개수"),
                                    fields.withPath("isPossibleCount").type(JsonFieldType.BOOLEAN).description("수량조절 가능여부"),
                                    fields.withPath("options").type(JsonFieldType.ARRAY).description("옵션 내용"),
                                    fields.withPath("options[].content").type(JsonFieldType.STRING).description("옵션명"),
                                    fields.withPath("options[].price").type(JsonFieldType.NUMBER).description("가격")
                            ),
                            responseFields(
                                    fieldWithPath("menuOptionGroupId").type(JsonFieldType.NUMBER).description("옵션그룹 ID")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 그룹 조회")
        void find() throws Exception {
            // given
            given(menuOptionGroupService.get(anyLong()))
                    .willReturn(MenuOptionGroup.builder()
                            .id(1L)
                            .name("옵션 그룹")
                            .build());

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/owner/menu-option-group/{menuOptionGroupId}", 1));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("옵션 그룹"))
                    .andDo(print())
                    .andDo(document("menu-option-group/find-one",
                            pathParameters(
                                    parameterWithName("menuOptionGroupId").description("옵션 그룹 ID")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("옵션 그룹 ID"),
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("옵션그룹명")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 그룹 전체 조회")
        void findAll() throws Exception {
            // given
            List<MenuOption> menuOptions1 = Arrays.asList(
                    MenuOption.builder().id(1L).content("옵션1").price(1000).position(1).build(),
                    MenuOption.builder().id(2L).content("옵션2").price(1000).position(2).build(),
                    MenuOption.builder().id(3L).content("옵션3").price(1000).position(3).build()
            );
            List<MenuOption> menuOptions2 = Arrays.asList(
                    MenuOption.builder().id(4L).content("옵션1").price(1000).position(1).build(),
                    MenuOption.builder().id(5L).content("옵션2").price(1000).position(2).build(),
                    MenuOption.builder().id(6L).content("옵션3").price(1000).position(3).build()
            );
            List<OptionGroupLinkMenu> menus = Arrays.asList(
                    OptionGroupLinkMenu.builder().menu(Menu.builder().name("메뉴1").build()).build(),
                    OptionGroupLinkMenu.builder().menu(Menu.builder().name("메뉴2").build()).build()
            );
            MenuOptionGroup menuOptionGroup1 = MenuOptionGroup.builder()
                    .id(1L)
                    .name("옵션그룹1")
                    .position(1)
                    .count(4)
                    .isPossibleCount(false)
                    .optionType(OptionType.REQUIRED)
                    .menuOptions(menuOptions1)
                    .linkMenus(menus)
                    .build();
            MenuOptionGroup menuOptionGroup2 = MenuOptionGroup.builder()
                    .id(2L)
                    .name("옵션그룹2")
                    .position(2)
                    .count(5)
                    .isPossibleCount(true)
                    .optionType(OptionType.OPTIONAL)
                    .menuOptions(menuOptions2)
                    .linkMenus(menus)
                    .build();
            given(menuOptionGroupService.getAll(anyLong())).willReturn(Arrays.asList(menuOptionGroup1, menuOptionGroup2));

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/owner/menu-option-group/shop/{shopId}", 1));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.menuOptionGroups").isArray())
                    .andExpect(jsonPath("$.menuOptionGroups.length()").value(2))
                    .andExpect(jsonPath("$.menuOptionGroups[0].menus").isArray())
                    .andExpect(jsonPath("$.menuOptionGroups[0].menus.length()").value(2))
                    .andExpect(jsonPath("$.menuOptionGroups[0].menuOptions").isArray())
                    .andExpect(jsonPath("$.menuOptionGroups[0].menuOptions.length()").value(3))
                    .andDo(print())
                    .andDo(document("menu-option-group/find-all",
                            pathParameters(
                                    parameterWithName("shopId").description("가게 ID")
                            ),
                            responseFields(
                                    fieldWithPath("menuOptionGroups").type(JsonFieldType.ARRAY).description("옵션그룹 리스트"),
                                    fieldWithPath("menuOptionGroups[].id").type(JsonFieldType.NUMBER).description("옵션그룹 ID"),
                                    fieldWithPath("menuOptionGroups[].name").type(JsonFieldType.STRING).description("옵션그룹명"),
                                    fieldWithPath("menuOptionGroups[].position").type(JsonFieldType.NUMBER).description("옵션그룹 정렬순서"),
                                    fieldWithPath("menuOptionGroups[].count").type(JsonFieldType.NUMBER).description("옵션 선택 가능 개수"),
                                    fieldWithPath("menuOptionGroups[].isPossibleCount").type(JsonFieldType.BOOLEAN).description("수량조절 가능여부"),
                                    fieldWithPath("menuOptionGroups[].optionType").type(JsonFieldType.STRING).description(generateLinkCode(OPTION_TYPE)),
                                    fieldWithPath("menuOptionGroups[].menuOptions").type(JsonFieldType.ARRAY).description("옵션 리스트"),
                                    fieldWithPath("menuOptionGroups[].menuOptions[].id").type(JsonFieldType.NUMBER).description("옵션 ID"),
                                    fieldWithPath("menuOptionGroups[].menuOptions[].content").type(JsonFieldType.STRING).description("옵션명"),
                                    fieldWithPath("menuOptionGroups[].menuOptions[].price").type(JsonFieldType.NUMBER).description("옵션 가격"),
                                    fieldWithPath("menuOptionGroups[].menuOptions[].position").type(JsonFieldType.NUMBER).description("옵션 정렬 순서"),
                                    fieldWithPath("menuOptionGroups[].menus").type(JsonFieldType.ARRAY).description("연결메뉴")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 그룹 수정")
        void update() throws Exception {
            // given
            doNothing().when(menuOptionGroupService).update(any());
            MenuOptionGroupUpdateRequest request = MenuOptionGroupUpdateRequest.builder()
                    .name("옵션그룹")
                    .build();

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/owner/menu-option-group/{menuOptionGroupId}/update", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            ConstrainedFields fields = new ConstrainedFields(MenuOptionGroupUpdateRequest.class);
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("menu-option-group/update",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuOptionGroupId").description("옵션그룹 ID")
                            ),
                            requestFields(
                                    fields.withPath("name").type(JsonFieldType.STRING).description("옵션그룹명")
                            )
                    ));
        }
        @Test
        @DisplayName("옵션 그룹 삭제")
        void delete() throws Exception {
            // given
            doNothing().when(menuOptionGroupService).delete(anyLong());

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/owner/menu-option-group/{menuOptionGroupId}/delete", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt));

            // then
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("menu-option-group/delete",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuOptionGroupId").description("옵션그룹 ID")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 그룹 메뉴 연결")
        void linkMenu() throws Exception {
            // given
            doNothing().when(menuOptionGroupService).linkMenu(anyLong(), anyList());
            MenuOptionGroupLinkMenuRequest request = MenuOptionGroupLinkMenuRequest.builder()
                    .menuIds(Arrays.asList(1L, 3L, 4L, 2L))
                    .build();

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.put("/owner/menu-option-group/{menuOptionGroupId}/link-menu", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            ConstrainedFields fields = new ConstrainedFields(MenuOptionGroupLinkMenuRequest.class);
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("menu-option-group/link-menu",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuOptionGroupId").description("옵션그룹 ID")
                            ),
                            requestFields(
                                    fields.withPath("menuIds").type(JsonFieldType.ARRAY).description("메뉴 ID 리스트")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 그룹 정렬 순서 변경")
        void changeOrder() throws Exception {
            // given
            doNothing().when(menuOptionGroupService).updatePosition(anyLong(), anyList());
            MenuOptionGroupUpdatePositionRequest request = MenuOptionGroupUpdatePositionRequest.builder()
                    .menuOptionGroupIds(Arrays.asList(4L, 2L, 1L, 3L, 5L))
                    .build();

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.put("/owner/menu-option-group/shop/{shopId}/change-position", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            ConstrainedFields fields = new ConstrainedFields(MenuOptionGroupUpdatePositionRequest.class);
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("menu-option-group/change-position",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("shopId").description("가게 ID")
                            ),
                            requestFields(
                                    fields.withPath("menuOptionGroupIds").type(JsonFieldType.ARRAY).description("옵션그룹 ID 리스트, 리스트 순서대로 정렬됨")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("옵션")
    class OptionTest {

        @Test
        @DisplayName("옵션 추가")
        void addOption() throws Exception {
            // given
            given(menuOptionService.create(any())).willReturn(1L);
            MenuOptionCreateRequest request = MenuOptionCreateRequest.builder()
                    .content("옵션1")
                    .price(1000)
                    .build();

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/owner/menu-option-group/{menuOptionGroupId}/add-option", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            ConstrainedFields fields = new ConstrainedFields(MenuOptionCreateRequest.class);
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.menuOptionId").value(1))
                    .andDo(print())
                    .andDo(document("menu-option-group/add-option",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuOptionGroupId").description("옵션그룹 ID")
                            ),
                            requestFields(
                                    fields.withPath("content").type(JsonFieldType.STRING).description("옵션명"),
                                    fields.withPath("price").type(JsonFieldType.NUMBER).description("가격")
                            ),
                            responseFields(
                                    fieldWithPath("menuOptionId").type(JsonFieldType.NUMBER).description("옵션 ID")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 조회")
        void getMenuOption() throws Exception {
            // given
            given(menuOptionService.get(anyLong()))
                    .willReturn(MenuOption.builder()
                            .id(1L)
                            .content("옵션1")
                            .price(1000)
                            .position(1)
                            .build());

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/owner/menu-option-group/option/{menuOptionId}", 1));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.content").value("옵션1"))
                    .andExpect(jsonPath("$.price").value(1000))
                    .andDo(print())
                    .andDo(document("menu-option-group/find-option",
                            pathParameters(
                                    parameterWithName("menuOptionId").description("옵션 ID")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("옵션 ID"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("옵션명"),
                                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 수정")
        void updateMenuOption() throws Exception {
            // given
            doNothing().when(menuOptionService).update(any());
            MenuOptionUpdateRequest request = MenuOptionUpdateRequest.builder()
                    .content("옵션1")
                    .price(2000)
                    .build();

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/owner/menu-option-group/option/{menuOptionId}/update", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            ConstrainedFields fields = new ConstrainedFields(MenuOptionUpdateRequest.class);
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("menu-option-group/update-option",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuOptionId").description("옵션 ID")
                            ),
                            requestFields(
                                    fields.withPath("content").type(JsonFieldType.STRING).description("옵션명"),
                                    fields.withPath("price").type(JsonFieldType.NUMBER).description("가격")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 삭제")
        void deleteMenuOption() throws Exception {
            // given
            doNothing().when(menuOptionService).delete(anyLong());

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/owner/menu-option-group/option/{menuOptionId}/delete", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt));

            // then
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("menu-option-group/delete-option",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuOptionId").description("옵션 ID")
                            )
                    ));
        }

        @Test
        @DisplayName("옵션 정렬 순서 변경")
        void changeOptionOrder() throws Exception {
            // given
            doNothing().when(menuOptionService).updatePosition(anyLong(), anyList());
            MenuOptionUpdatePositionRequest request = MenuOptionUpdatePositionRequest.builder()
                    .menuOptionIds(Arrays.asList(3L, 2L, 1L, 4L, 5L))
                    .build();

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.put("/owner/menu-option-group/{menuOptionGroupId}/change-option-position", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            ConstrainedFields fields = new ConstrainedFields(MenuOptionUpdatePositionRequest.class);
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("menu-option-group/change-option-position",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuOptionGroupId").description("옵션그룹 ID")
                            ),
                            requestFields(
                                    fields.withPath("menuOptionIds").type(JsonFieldType.ARRAY).description("옵션 ID 리스트, 리스트 순서대로 정렬됨")
                            )
                    ));
        }
    }

}