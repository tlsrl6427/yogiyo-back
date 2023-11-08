package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    public static class CloseDayDto {

        private int weekNumOfMonth;
        private Days dayOfWeek;

    }
}
