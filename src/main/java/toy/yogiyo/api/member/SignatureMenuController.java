package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #request.shopId)")
    public void setSignatureMenus(@Validated @RequestBody SignatureMenuSetRequest request) {
        List<SignatureMenu> signatureMenus = request.toSignatureMenus();
        signatureMenuService.deleteAll(request.getShopId());

        for (SignatureMenu signatureMenu : signatureMenus) {
            signatureMenuService.create(signatureMenu);
        }
    }

    @GetMapping("/shop/{shopId}")
    public SignatureMenusResponse getSignatureMenus(@PathVariable Long shopId) {
        List<SignatureMenu> signatureMenus = signatureMenuService.getAll(shopId);
        return SignatureMenusResponse.from(signatureMenus);
    }

    @DeleteMapping("/delete/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuPermissionEvaluator.hasWritePermission(authentication, #menuId)")
    public void delete(@PathVariable Long menuId) {
        signatureMenuService.delete(menuId);
    }

    @PutMapping("/{shopId}/change-position")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public void updatePosition(@PathVariable Long shopId, @Validated @RequestBody SignatureMenuUpdatePositionRequest request) {
        signatureMenuService.updateMenuPosition(shopId, request.toSignatureMenus());
    }

}
