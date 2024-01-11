package toy.yogiyo.core.deliveryplace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.deliveryplace.repository.DeliveryPlaceRepository;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPlace;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.deliveryplace.dto.DeliveryPlaceAddRequest;
import toy.yogiyo.core.deliveryplace.dto.DeliveryPriceUpdateRequest;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryPlaceService {

    private final DeliveryPlaceRepository deliveryPlaceRepository;
    private final ShopRepository shopRepository;


    @Transactional
    public void add(Long shopId, DeliveryPlaceAddRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        DeliveryPlace deliveryPlace = request.toDeliveryPlace();
        deliveryPlace.setShop(shop);

        deliveryPlaceRepository.save(deliveryPlace);
    }

    @Transactional(readOnly = true)
    public DeliveryPlace get(Long deliveryPlaceId) {
        return deliveryPlaceRepository.findById(deliveryPlaceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.DELIVERYPLACE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<DeliveryPlace> getByShop(Long shopId) {
        return deliveryPlaceRepository.findAllByShopId(shopId);
    }

    @Transactional
    public void updateDeliveryPrice(Long deliveryPlaceId, DeliveryPriceUpdateRequest request) {
        DeliveryPlace deliveryPlace = deliveryPlaceRepository.findById(deliveryPlaceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.DELIVERYPLACE_NOT_FOUND));

        deliveryPlace.updateDeliveryPrices(request.toDeliveryPriceInfos());
        deliveryPlace.getShop().updateDeliveryPriceAndTime(deliveryPlace);
    }

    @Transactional
    public void updateDeliveryPriceByShop(Long shopId, DeliveryPriceUpdateRequest request) {
        List<DeliveryPlace> deliveryPlaces = deliveryPlaceRepository.findAllByShopId(shopId);

        for (DeliveryPlace deliveryPlace : deliveryPlaces) {
            deliveryPlace.updateDeliveryPrices(request.toDeliveryPriceInfos());
            deliveryPlace.getShop().updateDeliveryPriceAndTime(deliveryPlace);
        }
    }

    @Transactional
    public void delete(Long deliveryPlaceId) {
        DeliveryPlace deliveryPlace = deliveryPlaceRepository.findById(deliveryPlaceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.DELIVERYPLACE_NOT_FOUND));

        deliveryPlaceRepository.delete(deliveryPlace);
    }

    @Transactional
    public void deleteAll(Long shopId) {
        deliveryPlaceRepository.deleteAllByShopId(shopId);
    }
}
