package toy.yogiyo.core.shop.dto.member;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.shop.domain.Days;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopInfoResponse {

    private Long id;
    private String name;
    private String noticeTitle;
    private String ownerNotice;
    private List<String> noticeImages;
//    private String noticeImages;
    private String callNumber;
    private String address;
    private List<DeliveryPriceInfoDto> deliveryPriceInfos;
    private List<BusinessHoursDto> businessHours;

    public void setBusinessHours(List<BusinessHoursDto> businessHours) {
        this.businessHours = businessHours;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryPriceInfoDto {

        private Long id;
        private int deliveryPrice;
        private int orderPrice;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessHoursDto {

        private Long id;
        private LocalTime breakTimeStart;
        private LocalTime breakTimeEnd;
        private LocalTime openTime;
        private LocalTime closeTime;
        private Days dayOfWeek;
        private boolean isOpen;

    }
}
