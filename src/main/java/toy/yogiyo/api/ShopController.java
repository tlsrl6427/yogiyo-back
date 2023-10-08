package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.login.LoginOwner;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.dto.*;
import toy.yogiyo.core.shop.service.ShopService;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;
    @PostMapping(value = "/register")
    public Long register(@LoginOwner Owner owner,
                         @RequestPart("shopData") ShopRegisterRequest request,
                         @RequestPart("icon") MultipartFile icon,
                         @RequestPart("banner") MultipartFile banner) throws IOException {

        return shopService.register(request, icon, banner, owner.getId());
    }

    @GetMapping("/{shopId}/info")
    public ShopInfoResponse getInfo(@PathVariable("shopId") Long shopId) {
        return shopService.getInfo(shopId);
    }
    @GetMapping("/{shopId}/notice")
    public ShopNoticeResponse getNotice(@PathVariable("shopId") Long shopId) {
        return shopService.getNotice(shopId);
    }
    @GetMapping("/{shopId}/business-hours")
    public ShopBusinessHourResponse getBusinessHours(@PathVariable("shopId") Long shopId) {
        return shopService.getBusinessHours(shopId);
    }
    @GetMapping("/{shopId}/delivery-price")
    public ShopDeliveryPriceResponse getDeliveryPrice(@PathVariable("shopId") Long shopId) {
        return shopService.getDeliveryPrice(shopId);
    }

    @PatchMapping("/{shopId}/info/update")
    public String updateInfo(@LoginOwner Owner owner,
                             @PathVariable Long shopId,
                             @RequestBody ShopUpdateRequest request) {

        shopService.updateShopInfo(shopId, owner.getId(), request);
        return "success";
    }

    @PatchMapping("/{shopId}/notice/update")
    public String updateNotice(@LoginOwner Owner owner,
                         @PathVariable Long shopId,
                         @RequestBody ShopNoticeUpdateRequest request) {

        shopService.updateNotice(shopId, owner.getId(), request);
        return "success";
    }

    @PatchMapping("/{shopId}/business-hours/update")
    public String updateNotice(@LoginOwner Owner owner,
                         @PathVariable Long shopId,
                         @RequestBody ShopBusinessHourUpdateRequest request) {

        shopService.updateBusinessHours(shopId, owner.getId(), request);
        return "success";
    }

    @PatchMapping("/{shopId}/delivery-price/update")
    public String updateNotice(@LoginOwner Owner owner,
                         @PathVariable Long shopId,
                         @RequestBody DeliveryPriceUpdateRequest request) {

        shopService.updateDeliveryPrice(shopId, owner.getId(), request);
        return "success";
    }

    @DeleteMapping("/{shopId}/delete")
    public String delete(@LoginOwner Owner owner, @PathVariable("shopId") Long shopId) {
        shopService.delete(shopId, owner.getId());
        return "success";
    }
}
