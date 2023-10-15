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
import toy.yogiyo.core.category.dto.CategoryShopCondition;
import toy.yogiyo.core.category.dto.CategoryShopResponse;
import toy.yogiyo.core.category.repository.CategoryShopQueryRepository;
import toy.yogiyo.core.category.repository.CategoryShopRepository;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
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
        List<Long> categoryIds = Arrays.asList(1L, 2L, 3L);

        when(categoryService.findCategory(anyLong())).thenReturn(new Category());
        when(categoryShopRepository.saveAll(anyCollection()))
                .thenReturn(Arrays.asList(new CategoryShop(), new CategoryShop(), new CategoryShop()));

        // when
        categoryShopService.save(categoryIds, shop);

        // then
        verify(categoryService, times(categoryIds.size())).findCategory(anyLong());
        verify(categoryShopRepository).saveAll(anyCollection());
        assertThat(shop.getCategoryShop().size()).isEqualTo(categoryIds.size());
    }

    @Test
    @DisplayName("카테고리 변경")
    void changeCategory() throws Exception {
        // given
        Shop shop = givenShopWithCategoryShop();
        List<Long> categoryIds = Arrays.asList(1L, 2L, 3L);

        doNothing().when(categoryShopRepository).deleteAll(anyList());

        // when
        categoryShopService.changeCategory(categoryIds, shop);

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
        CategoryShopCondition condition = new CategoryShopCondition(36.6732, 127.4491, null);

        when(categoryShopQueryRepository.findAround(category.getId(), condition, pageRequest))
                .thenReturn(new SliceImpl<>(categoryShops, pageRequest, false));


        // when
        Slice<CategoryShopResponse> result = categoryShopService.findShop(category.getId(), condition, pageRequest);

        // then
        verify(categoryShopQueryRepository).findAround(category.getId(), condition, pageRequest);

        CategoryShopResponse categoryShopResponse = result.getContent().get(0);
        assertThat(categoryShopResponse.getName()).isEqualTo(shop.getName());
        assertThat(categoryShopResponse.getIcon()).isEqualTo(shop.getIcon());
        assertThat(categoryShopResponse.getDeliveryTime()).isEqualTo(shop.getDeliveryTime());
        assertThat(categoryShopResponse.getDistance()).isBetween(165, 170);
        assertThat(categoryShopResponse.getStars()).isEqualTo((shop.getDeliveryScore() + shop.getQuantityScore() + shop.getTasteScore()) / 3);
        assertThat(categoryShopResponse.getReviewNum()).isEqualTo(shop.getReviewNum());
        assertThat(categoryShopResponse.getDeliveryPriceInfos()).containsAll(shop.getDeliveryPriceInfos().stream()
                .map(DeliveryPriceInfo::getDeliveryPrice)
                .collect(Collectors.toList()));
    }

    private Shop givenShop() {
        Shop shop = Shop.builder()
                .name("롯데리아")
                .icon("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .banner("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .ownerNotice("사장님 공지")
                .callNumber("010-1234-5678")
                .address("서울 강남구 영동대로 513")
                .deliveryTime(30)
                .build();

        shop.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(10000, 5000),
                new DeliveryPriceInfo(20000, 4000),
                new DeliveryPriceInfo(30000, 3000)));

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