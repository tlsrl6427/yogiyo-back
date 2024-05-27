package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.shop.dto.ShopDetailsRequest;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopRecentRequest;
import toy.yogiyo.core.shop.dto.member.ShopInfoResponse;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListRequest;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListResponse;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollResponse;
import toy.yogiyo.core.shop.repository.ShopRepository;
import toy.yogiyo.core.shop.service.ShopService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/member/shop")
public class ShopController {

    private final ShopService shopService;
    private final ShopRepository shopRepository;

    @GetMapping("/list")
    public ShopScrollListResponse getList(@Valid @ModelAttribute ShopScrollListRequest request){
        return shopService.getList(request);
    }

    @GetMapping("/details")
    public ShopDetailsResponse getDetails(@LoginUser Member member,
                                          @Valid @ModelAttribute ShopDetailsRequest request) {

        if(member.getId() == null) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
        return shopRepository.details(member.getId(), request);
    }

    @GetMapping("/{shopId}/info")
    public ShopInfoResponse getInfo(@PathVariable Long shopId, @RequestParam String code) {
        return shopRepository.info(shopId, code);
    }

    @GetMapping("/recentOrder")
    public List<ShopScrollResponse> getRecent(@LoginUser Member member, @Valid @ModelAttribute ShopRecentRequest request){
        return shopService.getRecentList(member, request);
    }

    @GetMapping("/search/{code}/{shopName}")
    public List<String> getSearchShopNames(@PathVariable String code, @PathVariable String shopName){
        return shopRepository.getSearchShopNames(code, shopName);
    }

//    @GetMapping("/search")
//    public List<ShopScrollResponse> getSearchShops(@Valid @ModelAttribute ShopScrollListRequest request){
//        return shopRepository.getSearchShops(request);
//    }
}
