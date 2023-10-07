package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.login.LoginOwner;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopRegisterRequest;
import toy.yogiyo.core.shop.dto.ShopUpdateRequest;
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

    @GetMapping("/{shopId}")
    public ShopDetailsResponse details(@PathVariable("shopId") Long shopId) {
        return shopService.getDetailInfo(shopId);
    }

    @PatchMapping("/{shopId}")
    public String update(@LoginOwner Owner owner,
                         @PathVariable("shopId") Long shopId,
                         @RequestBody ShopUpdateRequest request) {
        shopService.updateInfo(shopId, owner.getId(), request);
        return "success";
    }

    @DeleteMapping("/{shopId}")
    public String delete(@LoginOwner Owner owner, @PathVariable("shopId") Long shopId) {
        shopService.delete(shopId, owner.getId());
        return "success";
    }
}
