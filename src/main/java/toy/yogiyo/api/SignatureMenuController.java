package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.menu.dto.SignatureMenuChangeOrderRequest;
import toy.yogiyo.core.menu.dto.SignatureMenuSetRequest;
import toy.yogiyo.core.menu.dto.SignatureMenusResponse;
import toy.yogiyo.core.menu.service.SignatureMenuService;

import java.util.List;

@RestController
@RequestMapping("/signature-menu")
@RequiredArgsConstructor
public class SignatureMenuController {

    private final SignatureMenuService signatureMenuService;

    @PutMapping("/set")
    public String setSignatureMenus(@RequestBody SignatureMenuSetRequest request) {
        List<SignatureMenu> signatureMenus = request.toEntity();
        signatureMenuService.deleteAll(request.getShopId());

        for (SignatureMenu signatureMenu : signatureMenus) {
            signatureMenuService.create(signatureMenu);
        }

        return "success";
    }

    @GetMapping("/shop/{shopId}")
    public SignatureMenusResponse getSignatureMenus(@PathVariable Long shopId) {
        List<SignatureMenu> signatureMenus = signatureMenuService.findAll(shopId);
        return SignatureMenusResponse.from(signatureMenus);
    }

    @DeleteMapping("/delete/{menuId}")
    public String delete(@PathVariable Long menuId) {
        signatureMenuService.delete(menuId);
        return "success";
    }

    @PutMapping("/{shopId}/change-order")
    public String changeOrder(@PathVariable Long shopId, @RequestBody SignatureMenuChangeOrderRequest request) {
        signatureMenuService.changeMenuOrder(request.toEntity());
        return "success";
    }

}
