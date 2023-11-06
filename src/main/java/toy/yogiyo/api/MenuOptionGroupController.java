package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.dto.*;
import toy.yogiyo.core.menuoption.service.MenuOptionGroupService;
import toy.yogiyo.core.menuoption.service.MenuOptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu-option-group")
public class MenuOptionGroupController {

    private final MenuOptionGroupService menuOptionGroupService;
    private final MenuOptionService menuOptionService;

    // ============= 옵션 그룹 =============
    @PostMapping("/shop/{shopId}/add")
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public MenuOptionGroupCreateResponse create(@PathVariable Long shopId, @RequestBody MenuOptionGroupCreateRequest request) {
        Long optionGroupId = menuOptionGroupService.create(request.toMenuOptionGroup(shopId));
        return MenuOptionGroupCreateResponse.builder()
                .menuOptionGroupId(optionGroupId)
                .build();
    }

    @GetMapping("/shop/{shopId}")
    public MenuOptionGroupAllGetResponse getAll(@PathVariable Long shopId) {
        List<MenuOptionGroup> menuOptionGroups = menuOptionGroupService.getAll(shopId);
        return MenuOptionGroupAllGetResponse.from(menuOptionGroups);
    }

    @GetMapping("/{menuOptionGroupId}")
    public MenuOptionGroupGetResponse get(@PathVariable Long menuOptionGroupId) {
        MenuOptionGroup menuOptionGroup = menuOptionGroupService.get(menuOptionGroupId);
        return MenuOptionGroupGetResponse.from(menuOptionGroup);
    }

    @PatchMapping("/{menuOptionGroupId}/update")
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public String update(@PathVariable Long menuOptionGroupId, @RequestBody MenuOptionGroupUpdateRequest request) {
        menuOptionGroupService.update(request.toMenuOptionGroup(menuOptionGroupId));
        return "success";
    }

    @DeleteMapping("/{menuOptionGroupId}/delete")
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public String delete(@PathVariable Long menuOptionGroupId) {
        menuOptionGroupService.delete(menuOptionGroupId);
        return "success";
    }

    @PutMapping("/{menuOptionGroupId}/link-menu")
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public String linkMenu(@PathVariable Long menuOptionGroupId, @RequestBody MenuOptionGroupLinkMenuRequest request) {
        menuOptionGroupService.linkMenu(menuOptionGroupId, request.toMenus());
        return "success";
    }

    @PutMapping("/shop/{shopId}/change-order")
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public String changePosition(@PathVariable Long shopId, @RequestBody MenuOptionGroupUpdatePositionRequest request) {
        menuOptionGroupService.updatePosition(shopId, request.toMenuOptionGroups());
        return "success";
    }

    // ============= 옵션 =============
    @PostMapping("/{menuOptionGroupId}/add-option")
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public MenuOptionCreateResponse createOption(@PathVariable Long menuOptionGroupId, @RequestBody MenuOptionCreateRequest request) {
        Long menuOptionId = menuOptionService.create(request.toMenuOption(menuOptionGroupId));
        return MenuOptionCreateResponse.builder()
                .menuOptionId(menuOptionId)
                .build();
    }

    @GetMapping("/option/{menuOptionId}")
    public MenuOptionGetResponse getMenuOption(@PathVariable Long menuOptionId) {
        MenuOption menuOption = menuOptionService.get(menuOptionId);
        return MenuOptionGetResponse.from(menuOption);
    }

    @PatchMapping("/option/{menuOptionId}/update")
    @PreAuthorize("@menuOptionPermissionEvaluator.hasWritePermission(authentication, #menuOptionId)")
    public String updateMenuOption(@PathVariable Long menuOptionId, @RequestBody MenuOptionUpdateRequest request) {
        menuOptionService.update(request.toMenuOption(menuOptionId));
        return "success";
    }

    @DeleteMapping("/option/{menuOptionId}/delete")
    @PreAuthorize("@menuOptionPermissionEvaluator.hasWritePermission(authentication, #menuOptionId)")
    public String deleteMenuOption(@PathVariable Long menuOptionId) {
        menuOptionService.delete(menuOptionId);
        return "success";
    }

    @PutMapping("/{menuOptionGroupId}/change-option-order")
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public String changeOptionPosition(@PathVariable Long menuOptionGroupId, @RequestBody MenuOptionUpdatePositionRequest request) {
        menuOptionService.updatePosition(menuOptionGroupId, request.toMenuOptions());
        return "success";
    }
}
