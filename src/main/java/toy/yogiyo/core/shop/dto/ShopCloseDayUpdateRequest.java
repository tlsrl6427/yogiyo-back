package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import toy.yogiyo.core.shop.domain.CloseDay;
import toy.yogiyo.core.shop.domain.Days;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopCloseDayUpdateRequest {

    @NotNull
    @Builder.Default
    private List<CloseDayDto> closeDays = new ArrayList<>();

    public List<CloseDay> toCloseDays() {
        return closeDays.stream()
                .map(closeDay -> CloseDay.builder()
                        .dayOfWeek(closeDay.getDayOfWeek())
                        .weekNumOfMonth(closeDay.getWeekNumOfMonth())
                        .build())
                .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CloseDayDto {

        @Range(min = 1, max = 4, message = "1~4 사이의 숫자만 가능합니다.")
        private int weekNumOfMonth;
        private Days dayOfWeek;

    }
}
