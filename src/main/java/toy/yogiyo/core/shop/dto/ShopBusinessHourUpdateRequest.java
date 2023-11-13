package toy.yogiyo.core.shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.*;
import toy.yogiyo.core.shop.domain.BusinessHours;
import toy.yogiyo.core.shop.domain.Days;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopBusinessHourUpdateRequest {

    @NotNull @Valid
    private List<BusinessHoursDto> businessHours;

    public List<BusinessHours> toBusinessHours() {
        return businessHours.stream()
                .map(businessHours -> BusinessHours.builder()
                        .dayOfWeek(businessHours.getDayOfWeek())
                        .isOpen(businessHours.isOpen())
                        .openTime(businessHours.getOpenTime())
                        .closeTime(businessHours.getCloseTime())
                        .breakTimeStart(businessHours.getBreakTimeStart())
                        .breakTimeEnd(businessHours.getBreakTimeEnd())
                        .build())
                .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BusinessHoursDto {
        @NotNull
        private Days dayOfWeek;
        private boolean isOpen;

        @NotNull
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime openTime;

        @NotNull
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime closeTime;

        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime breakTimeStart;

        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime breakTimeEnd;

        @JsonProperty("isOpen")
        public boolean isOpen() {
            return isOpen;
        }
    }
}
