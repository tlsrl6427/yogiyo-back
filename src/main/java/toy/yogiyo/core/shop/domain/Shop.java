package toy.yogiyo.core.shop.domain;

import lombok.*;
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
    private long reviewNum;
    private long ownerReplyNum;

    private double tasteScore;
    private double quantityScore;
    private double deliveryScore;

    private String icon;
    private String banner;

    private String ownerNotice;
    private String businessHours;

    private String callNumber;
    private String address;

    private Double longitude;
    private Double latitude;

    private int deliveryTime;

    @Builder.Default
    @OneToMany(mappedBy = "shop")
    private List<CategoryShop> categoryShop = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Builder.Default
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryPriceInfo> deliveryPriceInfos = new ArrayList<>();


    public void changeOwner(Owner owner) {
        this.owner = owner;
    }

    public void changeInfo(String name, String ownerNotice, String businessHours, String callNumber, String address,
                           int deliveryTime, List<DeliveryPriceInfo> deliveryPriceInfos) {
        this.name = name;
        this.ownerNotice = ownerNotice;
        this.businessHours = businessHours;
        this.callNumber = callNumber;
        this.address = address;
        this.deliveryTime = deliveryTime;
        changeDeliveryPrices(deliveryPriceInfos);
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
}


