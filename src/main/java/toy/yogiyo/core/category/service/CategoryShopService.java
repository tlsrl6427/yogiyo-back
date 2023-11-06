package toy.yogiyo.core.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.util.LatLngDistanceCalculator;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.dto.CategoryShopCondition;
import toy.yogiyo.core.category.dto.CategoryShopResponse;
import toy.yogiyo.core.category.repository.CategoryShopQueryRepository;
import toy.yogiyo.core.category.repository.CategoryShopRepository;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryShopService {

    private final CategoryShopQueryRepository categoryShopQueryRepository;


    public Slice<CategoryShopResponse> findShop(Long categoryId, CategoryShopCondition condition, Pageable pageable) {
        Slice<CategoryShop> result = categoryShopQueryRepository.findAround(categoryId, condition, pageable);

        return result.map(categoryShop -> CategoryShopResponse.from(
                categoryShop,
                // 주문 주소와 가게 거리 계산 (meter)
                (int) LatLngDistanceCalculator.distance(
                        categoryShop.getShop().getLatitude(), categoryShop.getShop().getLongitude(),
                        condition.getLatitude(), condition.getLongitude())
        ));
    }

}
