package toy.yogiyo.core.shop.domain;

import lombok.*;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.category.domain.CategoryShop;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "shop_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private long wishNum;
    private long reviewNum;
    private long ownerReplyNum;

    private double tasteScore;
    private double quantityScore;
    private double deliveryScore;

    @Column(nullable = false)
    private String icon;
    @Column(nullable = false)
    private String banner;

    @Column(nullable = false)
    private String ownerNotice;
    @Column(nullable = false)
    private String businessHours;

    @Column(nullable = false)
    private String callNumber;
    @Column(nullable = false)
    private String address;


    private int deliveryTime;
    @Column(nullable = false)
    private String orderTypes;
    private int packagingPrice;

    //TODO : 연관관계 편의 메서드 작성
    @OneToMany(mappedBy = "shop")
    private List<CategoryShop> categoryShop = new ArrayList<>();

    //TODO : Owner 변경되면 nullable = false 달기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryPrice> deliveryPrices = new ArrayList<>();


    public Shop(String name, String icon, String banner, String ownerNotice, String businessHours, String callNumber, String address, int deliveryTime, String orderTypes, int packagingPrice) {
        this.name = name;
        this.icon = icon;
        this.banner = banner;
        this.ownerNotice = ownerNotice;
        this.businessHours = businessHours;
        this.callNumber = callNumber;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.orderTypes = orderTypes;
        this.packagingPrice = packagingPrice;
    }

    public void changeOwner(Owner owner) {
        this.owner = owner;
    }

    public void changeInfo(String name, String ownerNotice, String businessHours, String callNumber, String address,
                           int deliveryTime, String orderTypes, int packagingPrice, List<DeliveryPrice> deliveryPrices) {
        this.name = name;
        this.ownerNotice = ownerNotice;
        this.businessHours = businessHours;
        this.callNumber = callNumber;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.orderTypes = orderTypes;
        this.packagingPrice = packagingPrice;
        changeDeliveryPrices(deliveryPrices);
    }

    public void changeDeliveryPrices(List<DeliveryPrice> deliveryPrices) {
        this.deliveryPrices.clear();
        for (DeliveryPrice deliveryPrice : deliveryPrices) {
            this.deliveryPrices.add(deliveryPrice);
            deliveryPrice.setShop(this);
        }
    }


    @Getter @Setter
    @Entity
    public static class Owner {

        @Id @GeneratedValue
        private Long id;

        public Owner() {
        }

        public Owner(Long id) {
            this.id = id;
        }
    }
}


