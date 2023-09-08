package toy.yogiyo.core.shop.dto;

import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.shop.DeliveryPrice;

@Getter @Setter
public class DeliveryPriceDto {

    private int orderPrice;
    private int deliveryPrice;

    public DeliveryPriceDto() {
    }

    public DeliveryPriceDto(int orderPrice, int deliveryPrice) {
        this.orderPrice = orderPrice;
        this.deliveryPrice = deliveryPrice;
    }

    public DeliveryPrice toEntity() {
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        deliveryPrice.setOrderPrice(this.orderPrice);
        deliveryPrice.setDeliveryPrice(this.deliveryPrice);
        return deliveryPrice;
    }
}
