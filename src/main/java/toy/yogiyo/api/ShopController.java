package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.login.LoginOwner;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.dto.*;
import toy.yogiyo.core.shop.service.ShopService;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    @PostMapping(value = "/register")
    public ShopRegisterResponse register(@LoginOwner Owner owner,
                                         @RequestPart("shopData") ShopRegisterRequest request,
                                         @RequestPart("icon") MultipartFile icon,
                                         @RequestPart("banner") MultipartFile banner) throws IOException {

        Long shopId = shopService.register(request, icon, banner, owner);

        return ShopRegisterResponse.builder()
                .id(shopId)
                .build();
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

    @GetMapping("/{shopId}/close-day")
    public ShopCloseDayResponse getCloseDays(@PathVariable Long shopId) {
        return shopService.getCloseDays(shopId);
    }

    @PatchMapping("/{shopId}/call-number/update")
    public String updateCallNumber(@LoginOwner Owner owner,
                             @PathVariable Long shopId,
                             @RequestBody ShopUpdateCallNumberRequest request) {

        shopService.updateCallNumber(shopId, owner, request);
        return "success";
    }

    @PostMapping("/{shopId}/notice/update")
    public String updateNotice(@LoginOwner Owner owner,
                               @PathVariable Long shopId,
                               @RequestPart("noticeData") ShopNoticeUpdateRequest request,
                               @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {

        shopService.updateNotice(shopId, owner, request, imageFiles);
        return "success";
    }

    @PatchMapping("/{shopId}/business-hours/update")
    public String updateBusinessHours(@LoginOwner Owner owner,
                                      @PathVariable Long shopId,
                                      @RequestBody ShopBusinessHourUpdateRequest request) {

        shopService.updateBusinessHours(shopId, owner, request);
        return "success";
    }

    @PatchMapping("/{shopId}/delivery-price/update")
    public String updateNotice(@LoginOwner Owner owner,
                         @PathVariable Long shopId,
                         @RequestBody DeliveryPriceUpdateRequest request) {

        shopService.updateDeliveryPrice(shopId, owner, request);
        return "success";
    }

    @PatchMapping("/{shopId}/close-day/update")
    public String updateCloseDays(@LoginOwner Owner owner,
                                  @PathVariable Long shopId,
                                  @RequestBody ShopCloseDayUpdateRequest request) {

        shopService.updateCloseDays(shopId, owner, request);
        return "success";
    }

    @DeleteMapping("/{shopId}/delete")
    public String delete(@LoginOwner Owner owner, @PathVariable("shopId") Long shopId) {
        shopService.delete(shopId, owner);
        return "success";
    }

    @GetMapping("/list")
    public ShopScrollListResponse getList(@RequestBody ShopScrollListRequest request){
        return shopService.getList(request);
    }
}
