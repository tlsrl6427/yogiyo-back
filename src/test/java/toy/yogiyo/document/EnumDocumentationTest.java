package toy.yogiyo.document;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.document.api.EnumDocsController;
import toy.yogiyo.document.api.EnumDocsResponse;
import toy.yogiyo.document.utils.CustomResponseFieldsSnippet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

@WebMvcTest(EnumDocsController.class)
@ExtendWith(RestDocumentationExtension.class)
public class EnumDocumentationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void enums() throws Exception {
        ResultActions result = mockMvc.perform(get("/enum-docs"));
        EnumDocsResponse response = getData(result.andReturn());

        result.andDo(document("common/enum-response",
                customResponseFields("enum-response", beneathPath("days").withSubsectionId("days"),
                        attributes(key("title").value("영업 요일")),
                        enumConvertFieldDescriptor(response.getDays())
                ),
                customResponseFields("enum-response", beneathPath("optionType").withSubsectionId("optionType"),
                        attributes(key("title").value("옵션 유형")),
                        enumConvertFieldDescriptor(response.getOptionType())
                ),
                customResponseFields("enum-response", beneathPath("visible").withSubsectionId("visible"),
                        attributes(key("title").value("노출 유형")),
                        enumConvertFieldDescriptor(response.getVisible())
                ),
                customResponseFields("enum-response", beneathPath("adjustmentType").withSubsectionId("adjustmentType"),
                        attributes(key("title").value("조정 유형")),
                        enumConvertFieldDescriptor(response.getAdjustmentType())
                ),
                customResponseFields("enum-response", beneathPath("reviewSort").withSubsectionId("reviewSort"),
                        attributes(key("title").value("리뷰 정렬 기준")),
                        enumConvertFieldDescriptor(response.getReviewSort())
                ),
                customResponseFields("enum-response", beneathPath("reviewStatus").withSubsectionId("reviewStatus"),
                        attributes(key("title").value("리뷰 답변 상태")),
                        enumConvertFieldDescriptor(response.getReviewStatus())
                )
        ));
    }


    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {

        return enumValues.entrySet().stream()
                .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
                .toArray(FieldDescriptor[]::new);
    }

    private EnumDocsResponse getData(MvcResult result) throws IOException {
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                new TypeReference<EnumDocsResponse>() {
                });
    }

    public static CustomResponseFieldsSnippet customResponseFields(String type,
                                                                   PayloadSubsectionExtractor<?> subsectionExtractor,
                                                                   Map<String, Object> attributes, FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(type, subsectionExtractor, Arrays.asList(descriptors), attributes
                , true);
    }

}
