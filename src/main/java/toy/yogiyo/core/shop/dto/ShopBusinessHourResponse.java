package toy.yogiyo.core.shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.shop.domain.BusinessHours;
import toy.yogiyo.core.shop.domain.Days;
import toy.yogiyo.core.shop.domain.Shop;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ShopBusinessHourResponse {

    private List<BusinessHoursDto> businessHours;

    public static ShopBusinessHourResponse from(Shop shop) {
        return ShopBusinessHourResponse.builder()
                .businessHours(shop.getBusinessHours().stream()
                        .map(BusinessHoursDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    public static class BusinessHoursDto {
        private Days dayOfWeek;
        private boolean isOpen;
        private LocalTime openTime;
        private LocalTime closeTime;
        private LocalTime breakTimeStart;
        private LocalTime breakTimeEnd;

        public BusinessHoursDto(BusinessHours businessHours) {
            this.dayOfWeek = businessHours.getDayOfWeek();
            this.isOpen = businessHours.isOpen();
            this.openTime = businessHours.getOpenTime();
            this.closeTime = businessHours.getCloseTime();
            this.breakTimeStart = businessHours.getBreakTimeStart();
            this.breakTimeEnd = businessHours.getBreakTimeEnd();
        }

        @JsonProperty("isOpen")
        public boolean isOpen() {
            return isOpen;
        }
    }
}
