package toy.yogiyo.core.category.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.dto.CategoryDto;
import toy.yogiyo.core.category.dto.CategoryShopCondition;
import toy.yogiyo.core.category.dto.CategoryShopResponse;
import toy.yogiyo.core.category.repository.CategoryShopQueryRepository;
import toy.yogiyo.core.category.repository.CategoryShopRepository;
import toy.yogiyo.core.shop.domain.DeliveryPrice;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryShopServiceTest {

    @InjectMocks
    CategoryShopService categoryShopService;

    @Mock
    CategoryShopRepository categoryShopRepository;

    @Mock
    CategoryShopQueryRepository categoryShopQueryRepository;

    @Mock
    CategoryService categoryService;

    @Test
    @DisplayName("카테고리 지정")
    void setCategory() throws Exception {
        // given
        Shop shop = givenShop();
        List<CategoryDto> categoryDtos = Arrays.asList(
                new CategoryDto(1L, "치킨", "picture.png"),
                new CategoryDto(2L, "피자", "picture.png"),
                new CategoryDto(3L, "분식", "picture.png"));

        for (CategoryDto categoryDto : categoryDtos) {
            when(categoryService.findCategory(categoryDto.getId()))
                    .thenReturn(categoryDto.toEntity());
        }
        when(categoryShopRepository.saveAll(anyCollection()))
                .thenReturn(Arrays.asList(new CategoryShop(), new CategoryShop(), new CategoryShop()));

        // when
        categoryShopService.save(categoryDtos, shop);

        // then
        verify(categoryService, times(categoryDtos.size())).findCategory(anyLong());
        verify(categoryShopRepository).saveAll(anyCollection());
        assertThat(shop.getCategoryShop().size()).isEqualTo(categoryDtos.size());
    }

    @Test
    @DisplayName("카테고리 변경")
    void changeCategory() throws Exception {
        // given
        Shop shop = givenShopWithCategoryShop();
        List<CategoryDto> categoryDtos = Arrays.asList(
                new CategoryDto(1L, "치킨", "picture.png"),
                new CategoryDto(2L, "피자", "picture.png"),
                new CategoryDto(3L, "분식", "picture.png"));

        doNothing().when(categoryShopRepository).deleteAll(shop.getCategoryShop());

        // when
        categoryShopService.changeCategory(categoryDtos, shop);

        // then
        verify(categoryShopRepository).deleteAll(shop.getCategoryShop());
    }

    @Test
    @DisplayName("주변 상점 조회")
    void around() throws Exception {
        // given
        Category category = new Category(1L, "치킨", "picture.png");
        Shop shop = givenShop();
        List<CategoryShop> categoryShops = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            categoryShops.add(new CategoryShop((long) i, category, shop));
        }

        PageRequest pageRequest = PageRequest.of(0, 10);
        CategoryShopCondition condition = new CategoryShopCondition(36.6732, 127.4491, category.getId(), null);

        when(categoryShopQueryRepository.findAround(condition, pageRequest))
                .thenReturn(new SliceImpl<>(categoryShops, pageRequest, false));


        // when
        Slice<CategoryShopResponse> result = categoryShopService.findShop(condition, pageRequest);

        // then
        verify(categoryShopQueryRepository).findAround(condition, pageRequest);

        CategoryShopResponse categoryShopResponse = result.getContent().get(0);
        assertThat(categoryShopResponse.getName()).isEqualTo(shop.getName());
        assertThat(categoryShopResponse.getIcon()).isEqualTo(shop.getIcon());
        assertThat(categoryShopResponse.getDeliveryTime()).isEqualTo(shop.getDeliveryTime());
        assertThat(categoryShopResponse.getDistance()).isBetween(165, 170);
        assertThat(categoryShopResponse.getStars()).isEqualTo((shop.getDeliveryScore() + shop.getQuantityScore() + shop.getTasteScore()) / 3);
        assertThat(categoryShopResponse.getReviewNum()).isEqualTo(shop.getReviewNum());
        assertThat(categoryShopResponse.getDeliveryPrices()).containsAll(shop.getDeliveryPrices().stream()
                .map(DeliveryPrice::getDeliveryPrice)
                .collect(Collectors.toList()));
    }

    private Shop givenShop() {
        Shop shop = new Shop("롯데리아",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후 10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        shop.changeDeliveryPrices(Arrays.asList(
                new DeliveryPrice(10000, 5000),
                new DeliveryPrice(20000, 4000),
                new DeliveryPrice(30000, 3000)));

        shop.changeLatLng(36.674648, 127.448544);

        return shop;
    }

    private Shop givenShopWithCategoryShop() {
        Shop shop = givenShop();
        List<CategoryShop> categoryShop = shop.getCategoryShop();
        categoryShop.add(new CategoryShop());
        categoryShop.add(new CategoryShop());
        categoryShop.add(new CategoryShop());

        return shop;
    }

}