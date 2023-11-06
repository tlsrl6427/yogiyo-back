package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.menu.dto.SignatureMenuUpdatePositionRequest;
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
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #request.shopId)")
    public String setSignatureMenus(@RequestBody SignatureMenuSetRequest request) {
        List<SignatureMenu> signatureMenus = request.toSignatureMenus();
        signatureMenuService.deleteAll(request.getShopId());

        for (SignatureMenu signatureMenu : signatureMenus) {
            signatureMenuService.create(signatureMenu);
        }

        return "success";
    }

    @GetMapping("/shop/{shopId}")
    public SignatureMenusResponse getSignatureMenus(@PathVariable Long shopId) {
        List<SignatureMenu> signatureMenus = signatureMenuService.getAll(shopId);
        return SignatureMenusResponse.from(signatureMenus);
    }

    @DeleteMapping("/delete/{menuId}")
    @PreAuthorize("@menuPermissionEvaluator.hasWritePermission(authentication, #menuId)")
    public String delete(@PathVariable Long menuId) {
        signatureMenuService.delete(menuId);
        return "success";
    }

    @PutMapping("/{shopId}/change-order")
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public String changeOrder(@PathVariable Long shopId, @RequestBody SignatureMenuUpdatePositionRequest request) {
        signatureMenuService.updateMenuPosition(shopId, request.toSignatureMenus());
        return "success";
    }

}
