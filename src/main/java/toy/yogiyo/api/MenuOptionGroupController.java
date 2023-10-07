package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
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
    public MenuOptionGroupAddResponse add(@PathVariable Long shopId, @RequestBody MenuOptionGroupAddRequest request) {
        Long optionGroupId = menuOptionGroupService.add(request.toEntity(shopId));
        return MenuOptionGroupAddResponse.builder()
                .menuOptionGroupId(optionGroupId)
                .build();
    }

    @GetMapping("/shop/{shopId}")
    public MenuOptionGroupAllGetResponse findAll(@PathVariable Long shopId) {
        List<MenuOptionGroup> menuOptionGroups = menuOptionGroupService.findAll(shopId);
        return MenuOptionGroupAllGetResponse.from(menuOptionGroups);
    }

    @GetMapping("/{menuOptionGroupId}")
    public MenuOptionGroupGetResponse find(@PathVariable Long menuOptionGroupId) {
        MenuOptionGroup menuOptionGroup = menuOptionGroupService.find(menuOptionGroupId);
        return MenuOptionGroupGetResponse.from(menuOptionGroup);
    }

    @PatchMapping("/{menuOptionGroupId}/update")
    public String update(@PathVariable Long menuOptionGroupId, @RequestBody MenuOptionGroupUpdateRequest request) {
        menuOptionGroupService.update(request.toEntity(menuOptionGroupId));
        return "success";
    }

    @DeleteMapping("/{menuOptionGroupId}/delete")
    public String delete(@PathVariable Long menuOptionGroupId) {
        menuOptionGroupService.delete(menuOptionGroupId);
        return "success";
    }

    @PutMapping("/{menuOptionGroupId}/link-menu")
    public String linkMenu(@PathVariable Long menuOptionGroupId, @RequestBody MenuOptionGroupLinkMenuRequest request) {
        menuOptionGroupService.linkMenu(menuOptionGroupId, request.toEntity());
        return "success";
    }

    @PutMapping("/shop/{shopId}/change-order")
    public String changeOrder(@PathVariable Long shopId, @RequestBody MenuOptionGroupChangeOrderRequest request) {
        menuOptionGroupService.changeOrder(shopId, request.toEntity());
        return "success";
    }

    // ============= 옵션 =============
    @PostMapping("/{menuOptionGroupId}/add-option")
    public MenuOptionAddResponse addOption(@PathVariable Long menuOptionGroupId, @RequestBody MenuOptionAddRequest request) {
        Long menuOptionId = menuOptionService.add(request.toEntity(menuOptionGroupId));
        return MenuOptionAddResponse.builder()
                .menuOptionId(menuOptionId)
                .build();
    }

    @GetMapping("/option/{menuOptionId}")
    public MenuOptionGetResponse getMenuOption(@PathVariable Long menuOptionId) {
        MenuOption menuOption = menuOptionService.find(menuOptionId);
        return MenuOptionGetResponse.from(menuOption);
    }

    @PatchMapping("/option/{menuOptionId}/update")
    public String updateMenuOption(@PathVariable Long menuOptionId, @RequestBody MenuOptionUpdateRequest request) {
        menuOptionService.update(request.toEntity(menuOptionId));
        return "success";
    }

    @DeleteMapping("/option/{menuOptionId}/delete")
    public String deleteMenuOption(@PathVariable Long menuOptionId) {
        menuOptionService.delete(menuOptionId);
        return "success";
    }

    @PutMapping("/{menuOptionGroupId}/change-option-order")
    public String changeOptionOrder(@PathVariable Long menuOptionGroupId, @RequestBody MenuOptionChangeOrderRequest request) {
        menuOptionService.changeOrder(menuOptionGroupId, request.toEntity());
        return "success";
    }
}
