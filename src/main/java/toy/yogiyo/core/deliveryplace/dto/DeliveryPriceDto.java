package toy.yogiyo.core.deliveryplace.dto;

import lombok.Getter;
import lombok.Setter;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPriceInfo;

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

    public DeliveryPriceInfo toDeliveryPriceInfo() {
        DeliveryPriceInfo deliveryPriceInfo = new DeliveryPriceInfo();
        deliveryPriceInfo.setOrderPrice(this.orderPrice);
        deliveryPriceInfo.setDeliveryPrice(this.deliveryPrice);
        return deliveryPriceInfo;
    }
}
