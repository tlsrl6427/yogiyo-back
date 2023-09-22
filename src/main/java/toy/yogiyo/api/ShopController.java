package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    // TODO : Owner id 가져오는 로직 수정
    @PostMapping(value = "/register")
    public Long register(@RequestPart("shopData") ShopRegisterRequest request,
                         @RequestPart("icon") MultipartFile icon,
                         @RequestPart("banner") MultipartFile banner) throws IOException {

        return shopService.register(request, icon, banner, 1L);
    }

    @GetMapping("/{shopId}")
    public ShopDetailsResponse details(@PathVariable("shopId") Long shopId) {
        return shopService.getDetailInfo(shopId);
    }

    // TODO : Owner id 가져오는 로직 수정
    @PatchMapping("/{shopId}")
    public String update(@PathVariable("shopId") Long shopId, @RequestBody ShopUpdateRequest request) {
        shopService.updateInfo(shopId, 1L, request);
        return "success";
    }

    // TODO : Owner id 가져오는 로직 수정
    @DeleteMapping("/{shopId}")
    public String delete(@PathVariable("shopId") Long shopId) {
        shopService.delete(shopId, 1L);
        return "success";
    }
}
