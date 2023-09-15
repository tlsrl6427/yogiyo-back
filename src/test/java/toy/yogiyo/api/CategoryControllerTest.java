package toy.yogiyo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.dto.CategoryCreateRequest;
import toy.yogiyo.core.category.dto.CategoryResponse;
import toy.yogiyo.core.category.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    CategoryService categoryService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("카테고리 추가")
    void create() throws Exception {
        // given
        CategoryCreateRequest request = givenCreateRequest();
        when(categoryService.createCategory(any())).thenReturn(1L);

        // when
        ResultActions result = mockMvc.perform(
                multipart("/category/create")
                        .file((MockMultipartFile) request.getPicture())
                        .param("name", request.getName()));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print());
        verify(categoryService).createCategory(any());
    }

    @Test
    @DisplayName("카테고리 단건 조회")
    void findOne() throws Exception {
        // given
        Category category = givenCategory();
        when(categoryService.findCategory(category.getId())).thenReturn(category);


        // when
        ResultActions result = mockMvc.perform(
                get("/category/{categoryId}", category.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()))
                .andExpect(jsonPath("$.picture").value(category.getPicture()))
                .andDo(print());
        verify(categoryService).findCategory(category.getId());
    }

    @Test
    @DisplayName("모든 카테고리 조회")
    void findAll() throws Exception {
        // given
        List<CategoryResponse> categoryResponses = givenCategories().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
        when(categoryService.getCategories()).thenReturn(categoryResponses);

        // when
        ResultActions result = mockMvc.perform(
                get("/category/all"));

        // then
        for (int i = 0; i < categoryResponses.size(); i++) {
            result.andExpect(jsonPath("$.[%s].id", i).value(categoryResponses.get(i).getId()));
            result.andExpect(jsonPath("$.[%s].name", i).value(categoryResponses.get(i).getName()));
            result.andExpect(jsonPath("$.[%s].picture", i).value(categoryResponses.get(i).getPicture()));
        }
        result.andDo(print());
        verify(categoryService).getCategories();
    }

    @Test
    @DisplayName("카테고리 수정")
    void update() throws Exception {
        // given
        doNothing().when(categoryService).update(anyLong(), any());

        // when
        ResultActions result = mockMvc.perform(
                patch("/category/{categoryId}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print());
        verify(categoryService).update(anyLong(), any());
    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteCategory() throws Exception {
        // given
        doNothing().when(categoryService).delete(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete("/category/{categoryId}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print());
        verify(categoryService).delete(anyLong());
    }

    private Category givenCategory() {
        return new Category(1L, "분식", "/images/picture.png");
    }


    private List<Category> givenCategories() {
        return List.of(
                new Category(1L, "치킨", "/images/picture.png"),
                new Category(2L, "한식", "/images/picture.png"),
                new Category(3L, "중국집", "/images/picture.png"),
                new Category(4L, "버거", "/images/picture.png"),
                new Category(5L, "피자/양식", "/images/picture.png"),
                new Category(6L, "분식", "/images/picture.png"),
                new Category(7L, "족발/보쌈", "/images/picture.png"),
                new Category(8L, "카페/디저트", "/images/picture.png"),
                new Category(9L, "일식/돈까스", "/images/picture.png"),
                new Category(10L, "찜/탕", "/images/picture.png")
        );
    }

    private CategoryCreateRequest givenCreateRequest() {
        MockMultipartFile picture = new MockMultipartFile("picture", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
        CategoryCreateRequest createRequest = new CategoryCreateRequest();
        createRequest.setName("치킨");
        createRequest.setPicture(picture);

        return createRequest;
    }
}