package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public MenuOptionGroupCreateResponse create(@PathVariable Long shopId, @Validated @RequestBody MenuOptionGroupCreateRequest request) {
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public void update(@PathVariable Long menuOptionGroupId, @Validated @RequestBody MenuOptionGroupUpdateRequest request) {
        menuOptionGroupService.update(request.toMenuOptionGroup(menuOptionGroupId));
    }

    @DeleteMapping("/{menuOptionGroupId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public void delete(@PathVariable Long menuOptionGroupId) {
        menuOptionGroupService.delete(menuOptionGroupId);
    }

    @PutMapping("/{menuOptionGroupId}/link-menu")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public void linkMenu(@PathVariable Long menuOptionGroupId, @Validated @RequestBody MenuOptionGroupLinkMenuRequest request) {
        menuOptionGroupService.linkMenu(menuOptionGroupId, request.toMenus());
    }

    @PutMapping("/shop/{shopId}/change-position")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public void updatePosition(@PathVariable Long shopId, @Validated @RequestBody MenuOptionGroupUpdatePositionRequest request) {
        menuOptionGroupService.updatePosition(shopId, request.toMenuOptionGroups());
    }

    // ============= 옵션 =============
    @PostMapping("/{menuOptionGroupId}/add-option")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public MenuOptionCreateResponse createOption(@PathVariable Long menuOptionGroupId, @Validated @RequestBody MenuOptionCreateRequest request) {
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuOptionPermissionEvaluator.hasWritePermission(authentication, #menuOptionId)")
    public void updateMenuOption(@PathVariable Long menuOptionId, @Validated @RequestBody MenuOptionUpdateRequest request) {
        menuOptionService.update(request.toMenuOption(menuOptionId));
    }

    @DeleteMapping("/option/{menuOptionId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuOptionPermissionEvaluator.hasWritePermission(authentication, #menuOptionId)")
    public void deleteMenuOption(@PathVariable Long menuOptionId) {
        menuOptionService.delete(menuOptionId);
    }

    @PutMapping("/{menuOptionGroupId}/change-option-position")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuOptionGroupPermissionEvaluator.hasWritePermission(authentication, #menuOptionGroupId)")
    public void updateOptionPosition(@PathVariable Long menuOptionGroupId, @Validated @RequestBody MenuOptionUpdatePositionRequest request) {
        menuOptionService.updatePosition(menuOptionGroupId, request.toMenuOptions());
    }
}
