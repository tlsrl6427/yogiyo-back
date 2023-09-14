package toy.yogiyo.core.category.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.dto.CategoryCreateRequest;
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

    @Mock
    ImageFileHandler imageFileHandler;

    @BeforeAll
    static void beforeAll() {
        new ImageFileUtil().setPath("/images/");
    }

    @Nested
    @DisplayName("카테고리 추가")
    class Create {

        @Test
        @DisplayName("카테고리 추가 성공")
        void success() throws Exception {
            // given
            CategoryCreateRequest createRequest = givenCreateRequest();
            Category category = new Category(1L, createRequest.getName(), "/images/image.png");

            when(categoryRepository.existsByName(createRequest.getName())).thenReturn(false);
            when(imageFileHandler.store(createRequest.getPicture())).thenReturn("image.png");
            when(categoryRepository.save(any())).thenReturn(category);

            // when
            Long categoryId = categoryService.createCategory(createRequest);

            // then
            assertThat(categoryId).isEqualTo(category.getId());
            verify(categoryRepository).existsByName(createRequest.getName());
            verify(imageFileHandler).store(createRequest.getPicture());
            verify(categoryRepository).save(any());
        }

        @Test
        @DisplayName("카테고리명 중복이면 예외 발생")
        void failDuplicateName() throws Exception {
            // given
            CategoryCreateRequest createRequest = givenCreateRequest();
            when(categoryRepository.existsByName(createRequest.getName())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> categoryService.createCategory(createRequest))
                    .isInstanceOf(EntityExistsException.class);

            // then
            verify(categoryRepository).existsByName(createRequest.getName());
        }

        private CategoryCreateRequest givenCreateRequest() {
            MockMultipartFile picture = new MockMultipartFile("picture", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
            CategoryCreateRequest createRequest = new CategoryCreateRequest();
            createRequest.setName("치킨");
            createRequest.setPicture(picture);

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
            Category category = new Category(1L, updateRequest.getName(), "/images/image.png");

            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(imageFileHandler.remove(anyString())).thenReturn(true);
            when(imageFileHandler.store(updateRequest.getPicture())).thenReturn("image.png");

            // when
            categoryService.update(category.getId(), updateRequest);

            // then
            assertThat(category.getName()).isEqualTo(updateRequest.getName());
            assertThat(category.getPicture()).isEqualTo(ImageFileUtil.getFilePath("image.png"));
        }

        @Test
        @DisplayName("카테고리 수정 이름만 변경")
        void changeName() throws Exception {
            // given
            CategoryUpdateRequest updateRequest = givenUpdateRequest();
            updateRequest.setPicture(new MockMultipartFile("picture", "images.png", MediaType.IMAGE_PNG_VALUE, new byte[0]));
            Category category = new Category(1L, updateRequest.getName(), "/images/image.png");

            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

            // when
            categoryService.update(category.getId(), updateRequest);

            // then
            assertThat(category.getName()).isEqualTo(updateRequest.getName());
        }

        private CategoryUpdateRequest givenUpdateRequest() {
            MockMultipartFile picture = new MockMultipartFile("picture", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
            CategoryUpdateRequest updateRequest = new CategoryUpdateRequest();
            updateRequest.setName("한식");
            updateRequest.setPicture(picture);

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
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

            // when
            Category findCategory = categoryService.findCategory(category.getId());

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
            assertThatThrownBy(() -> categoryService.findCategory(1L))
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
            List<Category> findCategories = categoryService.getCategories();

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
            when(imageFileHandler.remove(any())).thenReturn(true);

            // when
            categoryService.delete(category.getId());

            // then
            verify(categoryRepository).findById(category.getId());
            verify(categoryRepository).delete(category);
            verify(imageFileHandler).remove(any());
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

        @Test
        @DisplayName("사진이 삭제되지 않으면 에러 발생")
        void failPicture() throws Exception {
            // given
            Category category = givenCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(imageFileHandler.remove(any())).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> categoryService.delete(category.getId()))
                    .isInstanceOf(FileIOException.class);

            // then
            verify(categoryRepository).findById(category.getId());
            verify(imageFileHandler).remove(any());
        }
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
}