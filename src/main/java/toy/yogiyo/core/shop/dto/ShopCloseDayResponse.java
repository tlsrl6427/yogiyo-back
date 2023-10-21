package toy.yogiyo.core.shop.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.shop.domain.CloseDay;
import toy.yogiyo.core.shop.domain.Days;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ShopCloseDayResponse {

    @Builder.Default
    private List<CloseDayDto> closeDays = new ArrayList<>();

    public static ShopCloseDayResponse from(Shop shop) {
        return ShopCloseDayResponse.builder()
                .closeDays(shop.getCloseDays().stream()
                        .map(CloseDayDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    public static class CloseDayDto {
        private int weekNumOfMonth;
        private Days dayOfWeek;

        public CloseDayDto(CloseDay closeDay) {
            this.weekNumOfMonth = closeDay.getWeekNumOfMonth();
            this.dayOfWeek = closeDay.getDayOfWeek();
        }
    }
}
