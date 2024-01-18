package toy.yogiyo.core.shop.dto.scroll;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import toy.yogiyo.common.dto.scroll.BaseScrollRequest;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.stream.Stream;

@Getter
@Builder
@AllArgsConstructor
public class ShopScrollListRequest {

    @NotNull(message = "유효하지 않은 카테고리가 입력되었습니다.")
    private ShopCategory category;
    @NotNull(message = "유효하지 않은 정렬기준이 입력되었습니다.")
    private SortOption sortOption;
    @PositiveOrZero
    private Integer deliveryPrice;
    @PositiveOrZero
    private Integer leastOrderPrice;
    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
    @NotNull
    private String code;

    private BigDecimal cursor;
    private Long subCursor;
    private Long size;

    public enum ShopCategory{
        Chicken("치킨"), Korean("한식"), Chinese("중국집"), Burger("버거"), Pizza("피자/양식"), School("분식"),
        Bossam("족발/보쌈"), Cafe("카페/디저트"), Japanese("일식/돈까스"), Soup("찜/탕"), Sushi("회/초밥"),
        Meat("고기/구이"), Midnight("야식"), Asian("아시안"), Sandwich("샌드위치"), Salad("샐러드"),
        Lunch("도시락/죽"), Franchise("프랜차이즈"), Single("1인분주문"), New("신규맛집");

        private final String categoryKoreanName;

        ShopCategory(String categoryKoreanName) {
            this.categoryKoreanName = categoryKoreanName;
        }

        @JsonCreator
        public static ShopCategory parsing(String category) {
            return Stream.of(ShopCategory.values())
                    .filter(shopCategory -> shopCategory.getCategoryKoreanName().equals(category))
                    .findFirst()
                    .orElse(null);
        }

        public String getCategoryKoreanName() {
            return categoryKoreanName;
        }
    }

    public enum SortOption{
        ORDER, REVIEW, SCORE, CLOSEST;

        @JsonCreator
        public static SortOption parsing(String sortOption) {
            return Stream.of(SortOption.values())
                    .filter(option -> option.name().equals(sortOption))
                    .findFirst()
                    .orElse(null);
        }
    }
}
