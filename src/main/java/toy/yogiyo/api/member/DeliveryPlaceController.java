package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.deliveryplace.domain.DeliveryPlace;
import toy.yogiyo.core.deliveryplace.dto.*;
import toy.yogiyo.core.deliveryplace.service.DeliveryPlaceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery-place")
public class DeliveryPlaceController {

    private final DeliveryPlaceService deliveryPlaceService;


    @PostMapping("/shop/{shopId}/add")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public void add(@PathVariable Long shopId,
                    @Validated @RequestBody DeliveryPlaceAddRequest request) {

        deliveryPlaceService.add(shopId, request);
    }

    @GetMapping("/{deliveryPlaceId}")
    public DeliveryPlaceGetResponse get(@PathVariable Long deliveryPlaceId) {
        DeliveryPlace deliveryPlace = deliveryPlaceService.get(deliveryPlaceId);
        return DeliveryPlaceGetResponse.from(deliveryPlace);
    }

    @GetMapping("/shop/{shopId}")
    public DeliveryPlaceListResponse getByShop(@PathVariable Long shopId) {
        List<DeliveryPlace> deliveryPlaces = deliveryPlaceService.getByShop(shopId);
        return DeliveryPlaceListResponse.from(deliveryPlaces);
    }

    @DeleteMapping("/{deliveryPlaceId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@deliveryPlacePermissionEvaluator.hasWritePermission(authentication, #deliveryPlaceId)")
    public void delete(@PathVariable Long deliveryPlaceId) {
        deliveryPlaceService.delete(deliveryPlaceId);
    }


    @PatchMapping("/{deliveryPlaceId}/delivery-price/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@deliveryPlacePermissionEvaluator.hasWritePermission(authentication, #deliveryPlaceId)")
    public void updateDeliveryPrice(@PathVariable Long deliveryPlaceId,
                                    @Validated @RequestBody DeliveryPriceUpdateRequest request) {

        deliveryPlaceService.updateDeliveryPrice(deliveryPlaceId, request);
    }

    @PatchMapping("/shop/{shopId}/delivery-price/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public void updateDeliveryPriceByShop(@PathVariable Long shopId,
                                          @Validated @RequestBody DeliveryPriceUpdateRequest request) {

        deliveryPlaceService.updateDeliveryPriceByShop(shopId, request);
    }

    @GetMapping("/{deliveryPriceId}/delivery-price")
    public DeliveryPriceResponse getDeliveryPrice(@PathVariable Long deliveryPriceId) {
        DeliveryPlace deliveryPlace = deliveryPlaceService.get(deliveryPriceId);
        return DeliveryPriceResponse.from(deliveryPlace);
    }

}
