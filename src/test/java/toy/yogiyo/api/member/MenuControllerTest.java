package toy.yogiyo.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
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
import toy.yogiyo.core.menu.dto.member.MenuDetailsGetResponse;
import toy.yogiyo.core.menu.repository.MenuQueryRepository;
import toy.yogiyo.core.menuoption.domain.OptionType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.yogiyo.document.utils.DocumentLinkGenerator.DocUrl.OPTION_TYPE;
import static toy.yogiyo.document.utils.DocumentLinkGenerator.generateLinkCode;

@WebMvcTest(MenuController.class)
@ExtendWith(RestDocumentationExtension.class)
class MenuControllerTest {


    @MockBean
    MenuQueryRepository menuQueryRepository;

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
    @DisplayName("")
    void getDetails() throws Exception {
        // given
        given(menuQueryRepository.getDetails(anyLong())).willReturn(
                MenuDetailsGetResponse.builder()
                        .id(1L)
                        .name("메뉴 1")
                        .content("메뉴 1 입니다.")
                        .picture("/images/hamburger.jpg")
                        .price(5000)
                        .reviewNum(20)
                        .optionGroups(List.of(
                                MenuDetailsGetResponse.OptionGroupDto.builder()
                                        .id(1L)
                                        .name("옵션그룹 1")
                                        .count(2)
                                        .optionType(OptionType.REQUIRED)
                                        .isPossibleCount(false)
                                        .options(List.of(
                                                MenuDetailsGetResponse.OptionDto.builder()
                                                        .id(12493L)
                                                        .content("옵션 1")
                                                        .price(2500)
                                                        .build(),
                                                MenuDetailsGetResponse.OptionDto.builder()
                                                        .id(12494L)
                                                        .content("옵션 2")
                                                        .price(3000)
                                                        .build(),
                                                MenuDetailsGetResponse.OptionDto.builder()
                                                        .id(12495L)
                                                        .content("옵션 3")
                                                        .price(2500)
                                                        .build()
                                        ))
                                        .build(),
                                MenuDetailsGetResponse.OptionGroupDto.builder()
                                        .id(2L)
                                        .name("옵션그룹 2")
                                        .count(2)
                                        .optionType(OptionType.OPTIONAL)
                                        .isPossibleCount(false)
                                        .options(List.of(
                                                MenuDetailsGetResponse.OptionDto.builder()
                                                        .id(12497L)
                                                        .content("옵션 1")
                                                        .price(1500)
                                                        .build(),
                                                MenuDetailsGetResponse.OptionDto.builder()
                                                        .id(12498L)
                                                        .content("옵션 2")
                                                        .price(2500)
                                                        .build(),
                                                MenuDetailsGetResponse.OptionDto.builder()
                                                        .id(12499L)
                                                        .content("옵션 3")
                                                        .price(500)
                                                        .build(),
                                                MenuDetailsGetResponse.OptionDto.builder()
                                                        .id(124500L)
                                                        .content("옵션 4")
                                                        .price(2000)
                                                        .build(),
                                                MenuDetailsGetResponse.OptionDto.builder()
                                                        .id(12501L)
                                                        .content("옵션 5")
                                                        .price(1000)
                                                        .build()
                                        ))
                                        .build()

                        ))
                        .build()
        );

        // when
        ResultActions result = mockMvc.perform(get("/member/menu/{menuId}", 1));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("member/menu/details",
                        pathParameters(
                                parameterWithName("menuId").description("메뉴 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("메뉴 ID"),
                                fieldWithPath("name").type(STRING).description("메뉴 이름"),
                                fieldWithPath("content").type(STRING).description("메뉴 설명"),
                                fieldWithPath("picture").type(STRING).description("메뉴 사진"),
                                fieldWithPath("price").type(NUMBER).description("메뉴 가격"),
                                fieldWithPath("reviewNum").type(NUMBER).description("메뉴 리뷰 개수"),
                                fieldWithPath("optionGroups[].id").type(NUMBER).description("옵션 그룹 ID"),
                                fieldWithPath("optionGroups[].name").type(STRING).description("옵션 그룹 명"),
                                fieldWithPath("optionGroups[].count").type(NUMBER).description("옵션 선택 가능 개수"),
                                fieldWithPath("optionGroups[].optionType").type(STRING).description(generateLinkCode(OPTION_TYPE)),
                                fieldWithPath("optionGroups[].possibleCount").type(BOOLEAN).description("수량 조절 가능 여부"),
                                fieldWithPath("optionGroups[].options[].id").type(NUMBER).description("옵션 ID"),
                                fieldWithPath("optionGroups[].options[].content").type(STRING).description("옵션 내용"),
                                fieldWithPath("optionGroups[].options[].price").type(NUMBER).description("옵션 가격")
                        )
                ));
    }


}