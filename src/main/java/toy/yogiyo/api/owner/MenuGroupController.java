package toy.yogiyo.api.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.dto.*;
import toy.yogiyo.core.menu.repository.MenuQueryRepository;
import toy.yogiyo.core.menu.service.MenuGroupService;
import toy.yogiyo.core.menu.service.MenuService;

import java.util.List;

@Component("menuGroupOwnerController")
@RestController
@RequestMapping("/owner/menu-group")
@RequiredArgsConstructor
public class MenuGroupController {

    private final MenuQueryRepository menuQueryRepository;
    private final MenuGroupService menuGroupService;
    private final MenuService menuService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #request.shopId)")
    public MenuGroupCreateResponse create(@Validated @RequestBody MenuGroupCreateRequest request) {
        Long menuGroupId = menuGroupService.create(request.toMenuGroup());
        return MenuGroupCreateResponse.builder()
                .id(menuGroupId)
                .build();
    }

    @PostMapping("/{menuGroupId}/add-menu")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public MenuCreateResponse createMenu(@PathVariable Long menuGroupId,
                                         @RequestPart(required = false) MultipartFile picture,
                                         @Validated @RequestPart("menuData") MenuCreateRequest request) {
        Menu menu = request.toMenu(menuGroupId);
        Long menuId = menuService.create(menu, picture);

        return MenuCreateResponse.builder()
                .id(menuId)
                .build();
    }

    @GetMapping("/shop/{shopId}")
    public MenuGroupsGetResponse getMenuGroups(@PathVariable Long shopId) {
        List<MenuGroup> menuGroups = menuGroupService.getMenuGroups(shopId);
        return MenuGroupsGetResponse.from(menuGroups);
    }

    @GetMapping("/{menuGroupId}")
    public MenuGroupGetResponse getMenuGroup(@PathVariable Long menuGroupId) {
        MenuGroup menuGroup = menuGroupService.get(menuGroupId);

        return MenuGroupGetResponse.from(menuGroup);
    }

    @GetMapping("/{menuGroupId}/menu")
    public MenuGroupGetMenusResponse getMenus(@PathVariable Long menuGroupId) {
        List<Menu> menus = menuService.getMenus(menuGroupId);
        return MenuGroupGetMenusResponse.from(menus);
    }

    @PatchMapping("/{menuGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public void update(@PathVariable Long menuGroupId, @Validated @RequestBody MenuGroupUpdateRequest request) {
        MenuGroup menuGroup = request.toMenuGroup(menuGroupId);
        menuGroupService.update(menuGroup);
    }

    @PostMapping("/update-menu/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuPermissionEvaluator.hasWritePermission(authentication, #menuId)")
    public void updateMenu(@PathVariable Long menuId,
                           @RequestPart(required = false) MultipartFile picture,
                           @Validated @RequestPart("menuData") MenuUpdateRequest request) {

        Menu menu = request.toMenu(menuId);
        menuService.update(menu, picture);
    }

    @DeleteMapping("/{menuGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public void delete(@PathVariable Long menuGroupId) {
        MenuGroup menuGroupParam = MenuGroup.builder()
                .id(menuGroupId)
                .build();

        menuGroupService.delete(menuGroupParam);
    }

    @DeleteMapping("/delete-menu/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuPermissionEvaluator.hasWritePermission(authentication, #menuId)")
    public void deleteMenu(@PathVariable Long menuId) {
        Menu menuParam = Menu.builder()
                .id(menuId)
                .build();

        menuService.delete(menuParam);
    }

    @PutMapping("/shop/{shopId}/change-position")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #shopId)")
    public void updatePosition(@PathVariable Long shopId, @Validated @RequestBody MenuGroupUpdatePositionRequest request) {
        List<MenuGroup> menuGroups = request.toMenuGroups();
        menuGroupService.updatePosition(shopId, menuGroups);
    }

    @PutMapping("/{menuGroupId}/change-menu-position")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public void updateMenuPosition(@PathVariable Long menuGroupId, @Validated @RequestBody MenuGroupUpdateMenuPositionRequest request) {
        List<Menu> menus = request.toMenus();
        menuGroupService.updateMenuPosition(menuGroupId, menus);
    }

    @PatchMapping("/{menuGroupId}/visible")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public void updateVisible(@PathVariable Long menuGroupId, @Validated @RequestBody MenuGroupVisibleUpdateRequest request) {
        menuGroupService.updateVisible(menuGroupId, request.getVisible());
    }

    @PatchMapping("/visible-menu/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@menuPermissionEvaluator.hasWritePermission(authentication, #menuId)")
    public void updateMenuVisible(@PathVariable Long menuId, @Validated @RequestBody MenuVisibleUpdateRequest request) {
        menuService.updateVisible(menuId, request.getVisible());
    }

    @GetMapping("/search")
    public MenuSearchResponse searchMenu(@Validated @ModelAttribute MenuSearchRequest request) {
        return menuQueryRepository.searchMenu(request);
    }

}
