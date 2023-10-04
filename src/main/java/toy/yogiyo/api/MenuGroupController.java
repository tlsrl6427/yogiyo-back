package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.domain.MenuGroupItem;
import toy.yogiyo.core.menu.dto.*;
import toy.yogiyo.core.menu.service.MenuGroupService;

import java.util.List;

@RestController
@RequestMapping("/menu-group")
@RequiredArgsConstructor
public class MenuGroupController {

    private final MenuGroupService menuGroupService;

    // =================== 점주 기능 ======================
    @PostMapping("/add")
    public MenuGroupAddResponse add(@RequestBody MenuGroupAddRequest request) {
        Long menuGroupId = menuGroupService.add(request.toEntity());
        return MenuGroupAddResponse.builder()
                .id(menuGroupId)
                .build();
    }

    @PostMapping("/{menuGroupId}/add-menu")
    public MenuAddResponse addMenu(@PathVariable Long menuGroupId, @RequestBody MenuAddRequest request) {
        Menu menu = request.toEntity();
        MenuGroupItem menuGroupItem = MenuGroupItem.builder()
                .menu(menu)
                .menuGroup(MenuGroup.builder().id(menuGroupId).build())
                .build();

        Long menuId = menuGroupService.addMenu(menuGroupItem);

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
        List<MenuGroupItem> menuGroupItems = menuGroupService.findMenus(menuGroupId);
        return MenuGroupGetMenusResponse.from(menuGroupItems);
    }

    @PatchMapping("/{menuGroupId}")
    public String update(@PathVariable Long menuGroupId, @RequestBody MenuGroupUpdateRequest request) {
        MenuGroup menuGroup = request.toEntity(menuGroupId);
        menuGroupService.update(menuGroup);
        return "success";
    }

    @DeleteMapping("/{menuGroupId}")
    public String delete(@PathVariable Long menuGroupId) {
        MenuGroup menuGroupParam = MenuGroup.builder()
                .id(menuGroupId)
                .build();

        menuGroupService.delete(menuGroupParam);

        return "success";
    }

    @DeleteMapping("/delete-menu/{menuId}")
    public String deleteMenu(@PathVariable Long menuId) {
        MenuGroupItem menuGroupItemParam = MenuGroupItem.builder()
                .menu(Menu.builder().id(menuId).build())
                .build();

        menuGroupService.deleteMenu(menuGroupItemParam);
        return "success";
    }

    @PatchMapping("/{menuGroupId}/change-menu-order")
    public String changeOrder(@PathVariable Long menuGroupId, @RequestBody MenuGroupChangeMenuOrderRequest request) {
        List<MenuGroupItem> menuGroupItems = request.toEntity();
        menuGroupService.changeMenuOrder(menuGroupId, menuGroupItems);
        return "success";
    }


    // =================== 고객 기능 ======================

}
