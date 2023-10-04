package toy.yogiyo.core.menu.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.domain.MenuGroupItem;
import toy.yogiyo.core.menu.repository.MenuGroupItemRepository;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    MenuGroupService menuGroupService;

    @Mock
    MenuGroupRepository menuGroupRepository;

    @Mock
    MenuGroupItemRepository menuGroupItemRepository;

    @Mock
    MenuService menuService;

    @Nested
    @DisplayName("메뉴 그룹")
    class MenuGroupTest {

        @Test
        @DisplayName("메뉴 그룹 추가")
        void add() throws Exception {
            // given
            MenuGroup menuGroup = MenuGroup.builder()
                    .id(1L)
                    .name("순살 메뉴")
                    .content("머스타드 + 소금 + 콜라 제공")
                    .shop(Shop.builder().id(2L).build())
                    .build();

            given(menuGroupRepository.save(any())).willReturn(menuGroup);

            // when
            Long addId = menuGroupService.add(menuGroup);

            // then
            assertThat(addId).isEqualTo(1L);
        }

        @Nested
        @DisplayName("메뉴 그룹 조회")
        class Find {

            @Test
            @DisplayName("메뉴 그룹 조회 성공")
            void success() throws Exception {
                // given
                MenuGroup menuGroup = MenuGroup.builder()
                        .id(1L)
                        .name("순살 메뉴")
                        .content("머스타드 + 소금 + 콜라 제공")
                        .shop(Shop.builder().id(2L).build())
                        .build();

                given(menuGroupRepository.findById(anyLong())).willReturn(Optional.of(menuGroup));

                // when
                MenuGroup findMenuGroup = menuGroupService.find(1L);

                // then
                assertThat(findMenuGroup).isEqualTo(menuGroup);
            }

            @Test
            @DisplayName("메뉴 그룹 조회 실패")
            void fail() throws Exception {
                // given
                given(menuGroupRepository.findById(anyLong())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> menuGroupService.find(1L))
                        .isInstanceOf(EntityNotFoundException.class);
            }
        }


        @Test
        @DisplayName("메뉴 그룹 정보 수정")
        void update() throws Exception {
            // given
            MenuGroup menuGroup = MenuGroup.builder()
                    .id(1L)
                    .name("순살 메뉴")
                    .content("머스타드 + 소금 + 콜라 제공")
                    .shop(Shop.builder().id(2L).build())
                    .build();

            MenuGroup updateParam = MenuGroup.builder()
                    .id(1L)
                    .name("뼈 메뉴")
                    .content("머스타드 + 소금 + 콜라 500ml 제공")
                    .build();

            given(menuGroupRepository.findById(anyLong())).willReturn(Optional.of(menuGroup));

            // when
            menuGroupService.update(updateParam);

            // then
            assertThat(menuGroup.getName()).isEqualTo("뼈 메뉴");
            assertThat(menuGroup.getContent()).isEqualTo("머스타드 + 소금 + 콜라 500ml 제공");
        }

        @Test
        @DisplayName("메뉴 그룹 삭제")
        void delete() throws Exception {
            // given
            MenuGroup menuGroup = MenuGroup.builder()
                    .id(1L)
                    .name("순살 메뉴")
                    .content("머스타드 + 소금 + 콜라 제공")
                    .shop(Shop.builder().id(2L).build())
                    .build();
            MenuGroup deleteParam = MenuGroup.builder().id(1L).build();

            given(menuGroupRepository.findById(anyLong())).willReturn(Optional.of(menuGroup));
            doNothing().when(menuGroupRepository).delete(any());

            // when
            menuGroupService.delete(deleteParam);

            // then
            then(menuGroupRepository).should().delete(menuGroup);
        }

    }
    @Nested
    @DisplayName("메뉴 그룹 메뉴")
    class MenuGroupMenuTest {

        @Test
        @DisplayName("메뉴 그룹 메뉴 추가")
        void addMenu() throws Exception {
            // given
            MenuGroupItem menuGroupItem = MenuGroupItem.builder()
                    .id(1L)
                    .menuGroup(MenuGroup.builder().id(1L).build())
                    .menu(Menu.builder().id(1L).build())
                    .build();

            given(menuGroupItemRepository.findMaxOrder(anyLong())).willReturn(5);
            given(menuGroupItemRepository.save(menuGroupItem)).willReturn(menuGroupItem);
            given(menuService.add(any())).willReturn(menuGroupItem.getMenu().getId());


            // when
            Long addMenuGroupItemId = menuGroupService.addMenu(menuGroupItem);

            // then
            assertThat(addMenuGroupItemId).isEqualTo(1L);
            assertThat(menuGroupItem.getPosition()).isEqualTo(6);
        }

        @Test
        @DisplayName("메뉴 그룹 메뉴 조회")
        void findMenus() throws Exception {
            // given

            List<MenuGroupItem> menuGroupItems = new ArrayList<>();

            MenuGroup menuGroup = MenuGroup.builder()
                    .id(1L)
                    .name("순살 메뉴")
                    .content("머스타드 + 소금 + 콜라 제공")
                    .shop(Shop.builder().id(1L).build())
                    .build();

            for (int i = 0; i < 5; i++) {

                Menu menu = Menu.builder()
                        .name("메뉴" + i)
                        .content("메뉴" + i + " 설명")
                        .price(10000)
                        .build();

                MenuGroupItem menuGroupItem = MenuGroupItem.builder()
                        .id(i + 1L)
                        .position(i + 1)
                        .menu(menu)
                        .menuGroup(menuGroup)
                        .build();

                menuGroupItems.add(menuGroupItem);
            }

            given(menuGroupItemRepository.findMenus(anyLong())).willReturn(menuGroupItems);


            // when
            List<MenuGroupItem> menus = menuGroupService.findMenus(menuGroup.getId());

            // then
            assertThat(menus.size()).isEqualTo(5);
            for (int i = 0; i < 5; i++) {
                assertThat(menus.get(i).getMenu().getName()).isEqualTo("메뉴" + i);
            }
        }

        @Test
        @DisplayName("메뉴 그룹 메뉴 순서 변경")
        void changeMenuOrder() throws Exception {
            // given
            List<MenuGroupItem> menuGroupItems = Arrays.asList(
                    MenuGroupItem.builder().id(1L).menu(Menu.builder().id(1L).build()).build(),
                    MenuGroupItem.builder().id(2L).menu(Menu.builder().id(2L).build()).build(),
                    MenuGroupItem.builder().id(3L).menu(Menu.builder().id(3L).build()).build(),
                    MenuGroupItem.builder().id(4L).menu(Menu.builder().id(4L).build()).build(),
                    MenuGroupItem.builder().id(5L).menu(Menu.builder().id(5L).build()).build()
            );

            given(menuGroupItemRepository.findMenus(anyLong())).willReturn(menuGroupItems);


            List<MenuGroupItem> params = Arrays.asList(
                    MenuGroupItem.builder().id(5L).menu(Menu.builder().id(5L).build()).build(),
                    MenuGroupItem.builder().id(4L).menu(Menu.builder().id(4L).build()).build(),
                    MenuGroupItem.builder().id(3L).menu(Menu.builder().id(3L).build()).build(),
                    MenuGroupItem.builder().id(2L).menu(Menu.builder().id(2L).build()).build(),
                    MenuGroupItem.builder().id(1L).menu(Menu.builder().id(1L).build()).build()
            );

            // when
            menuGroupService.changeMenuOrder(1L, params);

            // then
            assertThat(menuGroupItems.get(0).getPosition()).isEqualTo(5);
            assertThat(menuGroupItems.get(1).getPosition()).isEqualTo(4);
            assertThat(menuGroupItems.get(2).getPosition()).isEqualTo(3);
            assertThat(menuGroupItems.get(3).getPosition()).isEqualTo(2);
            assertThat(menuGroupItems.get(4).getPosition()).isEqualTo(1);
        }

        @Test
        @DisplayName("메뉴 그룹 메뉴 삭제")
        void deleteMenu() throws Exception {
            // given
            MenuGroupItem menuGroupItem = MenuGroupItem.builder()
                    .id(1L)
                    .position(1)
                    .menu(Menu.builder()
                            .id(2L)
                            .name("치킨")
                            .content("설명")
                            .price(15000)
                            .build())
                    .build();

            MenuGroupItem deleteParam = MenuGroupItem.builder().id(1L)
                    .menu(Menu.builder().id(2L).build())
                    .build();

            given(menuGroupItemRepository.findByMenuId(anyLong())).willReturn(Optional.of(menuGroupItem));
            doNothing().when(menuService).delete(any());

            // when
            menuGroupService.deleteMenu(deleteParam);

            // then
            then(menuService).should().delete(menuGroupItem.getMenu());
            then(menuGroupItemRepository).should().delete(menuGroupItem);
        }

    }
}