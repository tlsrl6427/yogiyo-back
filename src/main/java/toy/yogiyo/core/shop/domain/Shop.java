package toy.yogiyo.core.shop.domain;

import lombok.*;
import toy.yogiyo.common.converter.StringArrayConverter;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.owner.domain.Owner;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "shop_id")
    private Long id;

    private String name;

    private long wishNum;
    private long ownerReplyNum;

    // 리뷰
    private long reviewNum;
    private double tasteScore;
    private double quantityScore;
    private double deliveryScore;

    private String icon;
    private String banner;

    // 공지
    private String noticeTitle;
    private String ownerNotice;
    @Builder.Default
    @Convert(converter = StringArrayConverter.class)
    private List<String> noticeImages = new ArrayList<>();

    private String callNumber;
    private String address;

    private Double longitude;
    private Double latitude;

    private int deliveryTime;

    @Builder.Default
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryShop> categoryShop = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Builder.Default
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryPriceInfo> deliveryPriceInfos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusinessHours> businessHours = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CloseDay> closeDays = new ArrayList<>();


    public void changeOwner(Owner owner) {
        this.owner = owner;
    }

    public void changeCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public void changeNotice(String title, String notice, List<String> images) {
        this.noticeTitle = title;
        this.ownerNotice = notice;
        this.noticeImages = images;
    }

    public void changeBusinessHours(List<BusinessHours> businessHours) {
        this.businessHours.clear();
        for (BusinessHours businessHour : businessHours) {
            this.businessHours.add(businessHour);
            businessHour.changeShop(this);
        }
    }

    public void changeDeliveryPrices(List<DeliveryPriceInfo> deliveryPriceInfos) {
        this.deliveryPriceInfos.clear();
        for (DeliveryPriceInfo deliveryPriceInfo : deliveryPriceInfos) {
            this.deliveryPriceInfos.add(deliveryPriceInfo);
            deliveryPriceInfo.setShop(this);
        }
    }

    public void changeLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void changeCloseDays(List<CloseDay> closeDays) {
        this.closeDays.clear();
        for (CloseDay closeDay : closeDays) {
            this.closeDays.add(closeDay);
            closeDay.changeShop(this);
        }
    }
}


