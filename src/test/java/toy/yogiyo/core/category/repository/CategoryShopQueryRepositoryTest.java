package toy.yogiyo.core.category.repository;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.dto.CategoryShopCondition;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Disabled
class CategoryShopQueryRepositoryTest {

    @Autowired
    CategoryShopQueryRepository categoryShopQueryRepository;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryShopRepository categoryShopRepository;

    @Test
    @DisplayName("상점 조회")
    void findShop() throws Exception {
        // given
        Shop s = new Shop("맥도날드",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        s.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(5000, 3000),
                new DeliveryPriceInfo(10000, 2000),
                new DeliveryPriceInfo(20000, 1000)));

        s.changeLatLng(36.674648, 127.448544);

        shopRepository.save(s);

        Category c = new Category("햄버거", "picture.png");
        categoryRepository.save(c);

        CategoryShop cs = new CategoryShop(null, c, s);
        categoryShopRepository.save(cs);


        CategoryShopCondition condition = new CategoryShopCondition(36.6732, 127.4491, null);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Slice<CategoryShop> around = categoryShopQueryRepository.findAround(c.getId(), condition, pageRequest);

        // then
        assertThat(around.getContent().size()).isEqualTo(1);
        assertThat(around.getContent().get(0).getShop().getName()).isEqualTo("맥도날드");
    }

    @Test
    @DisplayName("상점 이름 검색")
    void findShopName() throws Exception {
        //given
        Category c = new Category("햄버거", "picture.png");
        categoryRepository.save(c);

        Shop s1 = new Shop("맥도날드",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        s1.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(5000, 3000),
                new DeliveryPriceInfo(10000, 2000),
                new DeliveryPriceInfo(20000, 1000)));

        s1.changeLatLng(36.674648, 127.448544);
        shopRepository.save(s1);
        categoryShopRepository.save(new CategoryShop(null, c, s1));

        Shop s2 = new Shop("롯데리아",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        s2.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(10000, 4000),
                new DeliveryPriceInfo(20000, 3000),
                new DeliveryPriceInfo(30000, 2000)));

        s2.changeLatLng(36.675306, 127.44358);
        shopRepository.save(s2);
        categoryShopRepository.save(new CategoryShop(null, c, s2));

        CategoryShopCondition condition = new CategoryShopCondition(36.6732, 127.4491, "맥도");
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Slice<CategoryShop> around = categoryShopQueryRepository.findAround(c.getId(), condition, pageRequest);

        // then
        assertThat(around.getContent().size()).isEqualTo(1);
        assertThat(around.getContent().get(0).getShop().getName()).isEqualTo("맥도날드");
    }

    @Test
    @DisplayName("상점 조회 - 최소 주문 금액 낮은순 정렬")
    void findShopOrderPriceMin() throws Exception {
        // given
        Category c = new Category("햄버거", "picture.png");
        categoryRepository.save(c);

        Shop s1 = new Shop("맥도날드",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        s1.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(5000, 3000),
                new DeliveryPriceInfo(10000, 2000),
                new DeliveryPriceInfo(20000, 1000)));

        s1.changeLatLng(36.674648, 127.448544);
        shopRepository.save(s1);
        categoryShopRepository.save(new CategoryShop(null, c, s1));

        Shop s2 = new Shop("롯데리아",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        s2.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(10000, 4000),
                new DeliveryPriceInfo(20000, 3000),
                new DeliveryPriceInfo(30000, 2000)));

        s2.changeLatLng(36.675306, 127.44358);
        shopRepository.save(s2);
        categoryShopRepository.save(new CategoryShop(null, c, s2));

        CategoryShopCondition condition = new CategoryShopCondition(36.6732, 127.4491, null);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "orderPrice"));

        // when
        Slice<CategoryShop> around = categoryShopQueryRepository.findAround(c.getId(), condition, pageRequest);

        // then
        assertThat(around.getContent().size()).isEqualTo(2);
        assertThat(around.getContent().get(0).getShop().getName()).isEqualTo("맥도날드");
        assertThat(around.getContent().get(1).getShop().getName()).isEqualTo("롯데리아");
    }

    @Test
    @DisplayName("상점 조회 - 최소 주문 금액 높은순 정렬")
    void findShopOrderPriceMax() throws Exception {
        // given
        Category c = new Category("햄버거", "picture.png");
        categoryRepository.save(c);

        Shop s1 = new Shop("맥도날드",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        s1.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(5000, 3000),
                new DeliveryPriceInfo(10000, 2000),
                new DeliveryPriceInfo(20000, 1000)));

        s1.changeLatLng(36.674648, 127.448544);
        shopRepository.save(s1);
        categoryShopRepository.save(new CategoryShop(null, c, s1));

        Shop s2 = new Shop("롯데리아",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        s2.changeDeliveryPrices(Arrays.asList(
                new DeliveryPriceInfo(10000, 4000),
                new DeliveryPriceInfo(20000, 3000),
                new DeliveryPriceInfo(30000, 2000)));

        s2.changeLatLng(36.675306, 127.44358);
        shopRepository.save(s2);
        categoryShopRepository.save(new CategoryShop(null, c, s2));

        CategoryShopCondition condition = new CategoryShopCondition(36.6732, 127.4491, null);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "orderPrice"));

        // when
        Slice<CategoryShop> around = categoryShopQueryRepository.findAround(c.getId(), condition, pageRequest);

        // then
        assertThat(around.getContent().size()).isEqualTo(2);
        assertThat(around.getContent().get(0).getShop().getName()).isEqualTo("롯데리아");
        assertThat(around.getContent().get(1).getShop().getName()).isEqualTo("맥도날드");
    }

}