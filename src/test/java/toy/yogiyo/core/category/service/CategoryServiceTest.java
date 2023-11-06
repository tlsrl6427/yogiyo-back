package toy.yogiyo.core.category.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.dto.CategoryCreateRequest;
import toy.yogiyo.core.category.dto.CategoryResponse;
import toy.yogiyo.core.category.dto.CategoryUpdateRequest;
import toy.yogiyo.core.category.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Nested
    @DisplayName("카테고리 추가")
    class Create {

        @Test
        @DisplayName("카테고리 추가 성공")
        void success() throws Exception {
            // given
            CategoryCreateRequest createRequest = givenCreateRequest();
            Category category = Category.builder()
                    .id(1L)
                    .name(createRequest.getName())
                    .build();

            when(categoryRepository.existsByName(anyString())).thenReturn(false);
            when(categoryRepository.save(any())).thenReturn(category);

            // when
            Long categoryId = categoryService.createCategory(createRequest);

            // then
            assertThat(categoryId).isEqualTo(category.getId());
            verify(categoryRepository).existsByName(createRequest.getName());
            verify(categoryRepository).save(any());
        }

        @Test
        @DisplayName("카테고리명 중복이면 예외 발생")
        void failDuplicateName() throws Exception {
            // given
            CategoryCreateRequest createRequest = givenCreateRequest();
            when(categoryRepository.existsByName(anyString())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> categoryService.createCategory(createRequest))
                    .isInstanceOf(EntityExistsException.class);

            // then
            verify(categoryRepository).existsByName(createRequest.getName());
        }

        private CategoryCreateRequest givenCreateRequest() {
            CategoryCreateRequest createRequest = new CategoryCreateRequest();
            createRequest.setName("치킨");

            return createRequest;
        }
    }

    @Nested
    @DisplayName("카테고리 수정")
    class Update {

        @Test
        @DisplayName("카테고리 수정 성공")
        void success() throws Exception {
            // given
            CategoryUpdateRequest updateRequest = givenUpdateRequest();
            Category category = Category.builder()
                    .id(1L)
                    .name(updateRequest.getName())
                    .build();

            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

            // when
            categoryService.update(category.getId(), updateRequest);

            // then
            assertThat(category.getName()).isEqualTo(updateRequest.getName());
        }

        private CategoryUpdateRequest givenUpdateRequest() {
            CategoryUpdateRequest updateRequest = new CategoryUpdateRequest();
            updateRequest.setName("한식");

            return updateRequest;
        }
    }

    @Nested
    @DisplayName("카테고리 조회")
    class Find {

        @Test
        @DisplayName("카테고리 조회 성공")
        void success() throws Exception {
            // given
            Category category = givenCategory();
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

            // when
            Category findCategory = categoryService.getCategory(category.getId());

            // then
            assertThat(findCategory).isEqualTo(category);
            verify(categoryRepository).findById(category.getId());
        }

        @Test
        @DisplayName("카테고리 조회 실패")
        void fail() throws Exception {
            // given
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.getCategory(1L))
                    .isInstanceOf(EntityNotFoundException.class);

            // then
            verify(categoryRepository).findById(anyLong());
        }

        @Test
        @DisplayName("모든 카테고리 조회")
        void findAll() throws Exception {
            // given
            List<Category> categories = givenCategories();
            when(categoryRepository.findAll()).thenReturn(categories);

            // when
            List<CategoryResponse> findCategories = categoryService.getCategories();

            // then
            assertThat(findCategories.size()).isEqualTo(categories.size());
            verify(categoryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("카테고리 삭제")
    class Delete {

        @Test
        @DisplayName("카테고리 삭제 성공")
        void success() throws Exception {
            // given
            Category category = givenCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            doNothing().when(categoryRepository).delete(category);

            // when
            categoryService.delete(category.getId());

            // then
            verify(categoryRepository).findById(category.getId());
            verify(categoryRepository).delete(category);
        }

        @Test
        @DisplayName("카테고리가 존재하지 않으면 에러 발생")
        void failNotFound() throws Exception {
            // given
            Category category = givenCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.delete(category.getId()))
                    .isInstanceOf(EntityNotFoundException.class);

            // then
            verify(categoryRepository).findById(category.getId());
        }
    }

    private Category givenCategory() {
        return new Category(1L, "분식");
    }

    private List<Category> givenCategories() {
        return List.of(
                new Category(1L, "치킨"),
                new Category(2L, "한식"),
                new Category(3L, "중국집"),
                new Category(4L, "버거"),
                new Category(5L, "피자/양식"),
                new Category(6L, "분식"),
                new Category(7L, "족발/보쌈"),
                new Category(8L, "카페/디저트"),
                new Category(9L, "일식/돈까스"),
                new Category(10L, "찜/탕")
        );
    }
}