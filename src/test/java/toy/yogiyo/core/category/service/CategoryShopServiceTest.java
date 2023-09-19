package toy.yogiyo.core.category.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.dto.CategoryDto;
import toy.yogiyo.core.category.repository.CategoryShopRepository;
import toy.yogiyo.core.shop.domain.DeliveryPrice;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryShopServiceTest {

    @InjectMocks
    CategoryShopService categoryShopService;

    @Mock
    CategoryShopRepository categoryShopRepository;

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