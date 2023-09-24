package toy.yogiyo.core.food.service;


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
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.food.domain.Food;
import toy.yogiyo.core.food.repository.FoodRepository;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @InjectMocks
    FoodService foodService;

    @Mock
    FoodRepository foodRepository;

    @Mock
    ImageFileHandler imageFileHandler;


    @BeforeAll
    static void beforeAll() {
        new ImageFileUtil().setPath("images");
    }


    @Test
    @DisplayName("음식 추가")
    void add() throws Exception {
        // given
        Food food = Food.builder()
                .id(1L)
                .name("피자")
                .content("피자 설명")
                .price(20000)
                .shop(Shop.builder().id(1L).build())
                .build();


        given(foodRepository.save(any())).willReturn(food);

        // when
        Long savedId = foodService.add(food);

        // then
        assertThat(savedId).isEqualTo(1L);
        then(foodRepository).should().save(food);
    }

    @Test
    @DisplayName("음식 사진 교체")
    void changePicture() throws Exception {
        // given
        Food food = Food.builder()
                .id(1L)
                .name("피자")
                .content("피자 설명")
                .price(20000)
                .picture("/images/picture.png")
                .build();

        MockMultipartFile picture = new MockMultipartFile("picture", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
        given(imageFileHandler.remove(anyString())).willReturn(true);
        given(imageFileHandler.store(any())).willReturn("new_picture.png");
        given(foodRepository.findById(anyLong())).willReturn(Optional.of(food));

        // when
        foodService.changePicture(1L, picture);

        // then
        assertThat(food.getPicture()).isEqualTo("/images/new_picture.png");
        then(imageFileHandler).should().store(picture);
    }

    @Nested
    @DisplayName("음식 조회")
    class Find {

        @Test
        @DisplayName("음식 조회 성공")
        void success() throws Exception {
            // given
            Food food = Food.builder()
                    .id(1L)
                    .name("피자")
                    .content("피자 설명")
                    .price(20000)
                    .shop(Shop.builder().id(1L).build())
                    .build();

            given(foodRepository.findById(anyLong())).willReturn(Optional.of(food));

            // when
            Food findFood = foodService.find(1L);

            // then
            assertThat(findFood.getId()).isEqualTo(1L);
            assertThat(findFood.getName()).isEqualTo("피자");
        }

        @Test
        @DisplayName("음식 조회 실패")
        void fail() throws Exception {
            // given
            given(foodRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> foodService.find(1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @DisplayName("음식 정보 수정")
    void update() throws Exception {
        // given
        Food food = Food.builder()
                .id(1L)
                .name("피자")
                .content("피자 설명")
                .price(20000)
                .shop(Shop.builder().id(1L).build())
                .build();

        Food updateParam = Food.builder()
                .id(1L)
                .name("치킨")
                .content("치킨 설명")
                .price(19000)
                .shop(Shop.builder().id(1L).build())
                .build();

        given(foodRepository.findById(any())).willReturn(Optional.of(food));

        // when
        foodService.update(updateParam);

        // then
        assertThat(food.getName()).isEqualTo("치킨");
        assertThat(food.getContent()).isEqualTo("치킨 설명");
        assertThat(food.getPrice()).isEqualTo(19000);
    }

    @Nested
    @DisplayName("음식 삭제")
    class Delete {

        @Test
        @DisplayName("음식 삭제 성공")
        void success() throws Exception {
            // given
            Food food = Food.builder()
                    .id(1L)
                    .name("피자")
                    .content("피자 설명")
                    .picture("picture.png")
                    .price(20000)
                    .build();

            Food deleteParam = Food.builder()
                    .id(1L)
                    .build();

            given(foodRepository.findById(anyLong())).willReturn(Optional.of(food));
            given(imageFileHandler.remove(anyString())).willReturn(true);
            doNothing().when(foodRepository).delete(any());

            // when
            foodService.delete(deleteParam);

            // then
            then(foodRepository).should().delete(food);
        }

        @Test
        @DisplayName("이미지가 삭제되지 않으면 예외 발생")
        void failPicture() throws Exception {
            // given
            Food food = Food.builder()
                    .id(1L)
                    .name("피자")
                    .content("피자 설명")
                    .picture("picture.png")
                    .price(20000)
                    .build();

            Food deleteParam = Food.builder()
                    .id(1L)
                    .build();

            given(foodRepository.findById(anyLong())).willReturn(Optional.of(food));
            given(imageFileHandler.remove(anyString())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> foodService.delete(deleteParam))
                    .isInstanceOf(FileIOException.class);
        }
    }
}