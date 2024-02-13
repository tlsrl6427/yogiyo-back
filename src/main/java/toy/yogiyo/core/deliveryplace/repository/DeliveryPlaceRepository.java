package toy.yogiyo.core.deliveryplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPlace;

import java.util.List;

public interface DeliveryPlaceRepository extends JpaRepository<DeliveryPlace, Long> {

    @Query("select dp from DeliveryPlace dp where dp.shop.id = :shopId")
    List<DeliveryPlace> findAllByShopId(Long shopId);

    @Modifying
    @Query("delete from DeliveryPlace dp where dp.shop.id = :shopId")
    void deleteAllByShopId(Long shopId);

    @Modifying
    @Query("update DeliveryPriceInfo dpi" +
            " set dpi.deliveryPrice = dpi.deliveryPrice + :value" +
            " where dpi.id in (" +
            "   select dpi2.id from DeliveryPriceInfo dpi2" +
            "   join dpi2.deliveryPlace dp" +
            "   join dp.shop s" +
            "   where s.id = :shopId" +
            " )")
    void bulkAdjustmentDeliveryPrice(Long shopId, int value);

    @Modifying
    @Query("update DeliveryPriceInfo dpi" +
            " set dpi.orderPrice = dpi.orderPrice + :value" +
            " where dpi.id in (" +
            "   select dpi2.id from DeliveryPriceInfo dpi2" +
            "   join dpi2.deliveryPlace dp" +
            "   join dp.shop s" +
            "   where s.id = :shopId" +
            " )")
    void bulkAdjustmentOrderPrice(Long shopId, int value);
}