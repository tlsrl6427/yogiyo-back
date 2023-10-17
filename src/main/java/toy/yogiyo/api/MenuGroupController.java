package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.dto.*;
import toy.yogiyo.core.menu.service.MenuGroupService;
import toy.yogiyo.core.menu.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/menu-group")
@RequiredArgsConstructor
public class MenuGroupController {

    private final MenuGroupService menuGroupService;
    private final MenuService menuService;

    // =================== 점주 기능 ======================
    @PostMapping("/add")
    @PreAuthorize("@shopPermissionEvaluator.hasWritePermission(authentication, #request.shopId)")
    public MenuGroupAddResponse add(@RequestBody MenuGroupAddRequest request) {
        Long menuGroupId = menuGroupService.add(request.toEntity());
        return MenuGroupAddResponse.builder()
                .id(menuGroupId)
                .build();
    }

    @PostMapping("/{menuGroupId}/add-menu")
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public MenuAddResponse addMenu(@PathVariable Long menuGroupId, @RequestBody MenuAddRequest request) {
        Menu menu = request.toEntity(menuGroupId);
        Long menuId = menuService.add(menu);

        return MenuAddResponse.builder()
                .id(menuId)
                .build();
    }

    @GetMapping("/shop/{shopId}")
    public MenuGroupsGetResponse getMenuGroups(@PathVariable Long shopId) {
        List<MenuGroup> menuGroups = menuGroupService.findMenuGroups(shopId);
        return MenuGroupsGetResponse.from(menuGroups);
    }

    @GetMapping("/{menuGroupId}")
    public MenuGroupGetResponse getMenuGroup(@PathVariable Long menuGroupId) {
        MenuGroup menuGroup = menuGroupService.find(menuGroupId);

        return MenuGroupGetResponse.from(menuGroup);
    }

    @GetMapping("/{menuGroupId}/menu")
    public MenuGroupGetMenusResponse getMenus(@PathVariable Long menuGroupId) {
        List<Menu> menus = menuService.findMenus(menuGroupId);
        return MenuGroupGetMenusResponse.from(menus);
    }

    @PatchMapping("/{menuGroupId}")
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public String update(@PathVariable Long menuGroupId, @RequestBody MenuGroupUpdateRequest request) {
        MenuGroup menuGroup = request.toEntity(menuGroupId);
        menuGroupService.update(menuGroup);
        return "success";
    }

    @DeleteMapping("/{menuGroupId}")
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public String delete(@PathVariable Long menuGroupId) {
        MenuGroup menuGroupParam = MenuGroup.builder()
                .id(menuGroupId)
                .build();

        menuGroupService.delete(menuGroupParam);

        return "success";
    }

    @DeleteMapping("/delete-menu/{menuId}")
    @PreAuthorize("@menuPermissionEvaluator.hasWritePermission(authentication, #menuId)")
    public String deleteMenu(@PathVariable Long menuId) {
        Menu menuParam = Menu.builder()
                .id(menuId)
                .build();

        menuService.delete(menuParam);
        return "success";
    }

    @PatchMapping("/{menuGroupId}/change-menu-order")
    @PreAuthorize("@menuGroupPermissionEvaluator.hasWritePermission(authentication, #menuGroupId)")
    public String changeOrder(@PathVariable Long menuGroupId, @RequestBody MenuGroupChangeMenuOrderRequest request) {
        List<Menu> menus = request.toEntity();
        menuGroupService.changeMenuOrder(menuGroupId, menus);
        return "success";
    }


    // =================== 고객 기능 ======================

}
