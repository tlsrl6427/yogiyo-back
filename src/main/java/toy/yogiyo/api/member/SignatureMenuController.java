package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.menu.dto.SignatureMenuSetRequest;
import toy.yogiyo.core.menu.dto.SignatureMenuUpdatePositionRequest;
import toy.yogiyo.core.menu.dto.SignatureMenusResponse;
import toy.yogiyo.core.menu.service.SignatureMenuService;

import java.util.List;

@RestController
@RequestMapping("/member/signature-menu")
@RequiredArgsConstructor
public class SignatureMenuController {

    private final SignatureMenuService signatureMenuService;

    @GetMapping("/shop/{shopId}")
    public SignatureMenusResponse getSignatureMenus(@PathVariable Long shopId) {
        List<SignatureMenu> signatureMenus = signatureMenuService.getAll(shopId);
        return SignatureMenusResponse.from(signatureMenus);
    }

}
