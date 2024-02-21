package toy.yogiyo.api.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.login.LoginOwner;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.dto.*;
import toy.yogiyo.core.shop.service.ShopService;

import java.io.IOException;
import java.util.List;

@Component("shopOwnerController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/owner/shop")
public class ShopController {

    private final ShopService shopService;

    @PostMapping(value = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ShopRegisterResponse register(@LoginOwner Owner owner,
                                         @Validated @RequestPart("shopData") ShopRegisterRequest request,
                                         @RequestPart("icon") MultipartFile icon,
                                         @RequestPart("banner") MultipartFile banner) {

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

    @GetMapping("/{shopId}/close-day")
    public ShopCloseDayResponse getCloseDays(@PathVariable Long shopId) {
        return shopService.getCloseDays(shopId);
    }

    @PatchMapping("/{shopId}/call-number/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCallNumber(@LoginOwner Owner owner,
                                 @PathVariable Long shopId,
                                 @Validated @RequestBody ShopUpdateCallNumberRequest request) {

        shopService.updateCallNumber(shopId, owner, request);
    }

    @PostMapping("/{shopId}/notice/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNotice(@LoginOwner Owner owner,
                             @PathVariable Long shopId,
                             @Validated @RequestPart("noticeData") ShopNoticeUpdateRequest request,
                             @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {

        shopService.updateNotice(shopId, owner, request, imageFiles);
    }

    @PatchMapping("/{shopId}/business-hours/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBusinessHours(@LoginOwner Owner owner,
                                    @PathVariable Long shopId,
                                    @Validated @RequestBody ShopBusinessHourUpdateRequest request) {

        shopService.updateBusinessHours(shopId, owner, request);
    }


    @PatchMapping("/{shopId}/close-day/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCloseDays(@LoginOwner Owner owner,
                                @PathVariable Long shopId,
                                @Validated @RequestBody ShopCloseDayUpdateRequest request) {

        shopService.updateCloseDays(shopId, owner, request);
    }

    @PatchMapping("/{shopId}/temp-close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void tempClose(@LoginOwner Owner owner,
                          @PathVariable Long shopId,
                          @Validated @RequestBody ShopTempCloseRequest request) {

        shopService.tempClose(shopId, owner, request);
    }


    @DeleteMapping("/{shopId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginOwner Owner owner, @PathVariable("shopId") Long shopId) {
        shopService.delete(shopId, owner);
    }


    @GetMapping
    public List<OwnerShopResponse> getShops(@LoginOwner Owner owner) {
        return shopService.getOwnerShops(owner);
    }
}
