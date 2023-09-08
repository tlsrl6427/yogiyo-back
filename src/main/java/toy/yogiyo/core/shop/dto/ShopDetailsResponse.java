package toy.yogiyo.core.shop.dto;

import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.shop.Shop;

@Getter @Setter
public class ShopDetailsResponse {
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

    private int deliveryTime;
    private int leastOrderPrice;
    private String orderTypes;
    private int deliveryPrice;
    private int packagingPrice;

    public static ShopDetailsResponse from(Shop shop) {
        ShopDetailsResponse response = new ShopDetailsResponse();

        response.setId(shop.getId());
        response.setName(shop.getName());
        response.setWishNum(shop.getWishNum());
        response.setReviewNum(shop.getReviewNum());
        response.setOwnerReplyNum(shop.getOwnerReplyNum());
        response.setTasteScore(shop.getTasteScore());
        response.setQuantityScore(shop.getQuantityScore());
        response.setDeliveryScore(shop.getDeliveryScore());
        response.setIcon(shop.getIcon());
        response.setBanner(shop.getBanner());
        response.setOwnerNotice(shop.getOwnerNotice());
        response.setBusinessHours(shop.getBusinessHours());
        response.setCallNumber(shop.getCallNumber());
        response.setAddress(shop.getAddress());
        response.setDeliveryTime(shop.getDeliveryTime());
        response.setLeastOrderPrice(shop.getLeastOrderPrice());
        response.setOrderTypes(shop.getOrderTypes());
        response.setDeliveryPrice(shop.getDeliveryPrice());
        response.setPackagingPrice(shop.getPackagingPrice());

        return response;
    }
}
