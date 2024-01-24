package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListRequest;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListResponse;
import toy.yogiyo.core.shop.service.ShopService;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/member/shop")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/list")
    public ShopScrollListResponse getList(@Valid @ModelAttribute ShopScrollListRequest request){
        return shopService.getList(request);
    }

}
