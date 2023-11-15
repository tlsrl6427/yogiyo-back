package toy.yogiyo.core.menu.service;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.repository.MenuRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    MenuService menuService;

    @Mock
    MenuRepository menuRepository;

    @Mock
    ImageFileHandler imageFileHandler;


    @BeforeAll
    static void beforeAll() {
        new ImageFileUtil().setPath("images");
    }


    @Test
    @DisplayName("메뉴 추가")
    void add() throws Exception {
        // given
        Menu menu = Menu.builder()
                .id(1L)
                .name("피자")
                .content("피자 설명")
                .price(20000)
                .menuGroup(MenuGroup.builder().id(1L).build())
                .build();

        MockMultipartFile picture = new MockMultipartFile("picture", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
        given(menuRepository.findMaxOrder(anyLong())).willReturn(null);
        given(imageFileHandler.store(any())).willReturn("new_picture.png");
        given(menuRepository.save(any())).willReturn(menu);

        // when
        Long savedId = menuService.create(menu, picture);

        // then
        assertThat(savedId).isEqualTo(1L);
        assertThat(menu.getPicture()).isEqualTo("/images/new_picture.png");
        then(menuRepository).should().save(menu);
        then(imageFileHandler).should().store(picture);
    }

    @Nested
    @DisplayName("메뉴 조회")
    class Find {

        @Test
        @DisplayName("메뉴 조회 성공")
        void success() throws Exception {
            // given
            Menu menu = Menu.builder()
                    .id(1L)
                    .name("피자")
                    .content("피자 설명")
                    .price(20000)
                    .build();

            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));

            // when
            Menu findMenu = menuService.get(1L);

            // then
            assertThat(findMenu.getId()).isEqualTo(1L);
            assertThat(findMenu.getName()).isEqualTo("피자");
        }

        @Test
        @DisplayName("메뉴 조회 실패")
        void fail() throws Exception {
            // given
            given(menuRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> menuService.get(1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("메뉴 그룹 하위 메뉴 조회")
        void findMenus() throws Exception {
            // given
            MenuGroup menuGroup = MenuGroup.builder()
                    .id(1L)
                    .build();

            List<Menu> menus = List.of(
                    Menu.builder().name("메뉴 1").content("메뉴 1").price(10000).menuGroup(menuGroup).build(),
                    Menu.builder().name("메뉴 2").content("메뉴 2").price(10000).menuGroup(menuGroup).build(),
                    Menu.builder().name("메뉴 3").content("메뉴 3").price(10000).menuGroup(menuGroup).build(),
                    Menu.builder().name("메뉴 4").content("메뉴 4").price(10000).menuGroup(menuGroup).build()
            );

            given(menuRepository.findMenus(anyLong())).willReturn(menus);


            // when
            List<Menu> findMenus = menuService.getMenus(menuGroup.getId());

            // then
            assertThat(findMenus.size()).isEqualTo(4);
            assertThat(findMenus.get(0).getName()).isEqualTo("메뉴 1");
            assertThat(findMenus.get(1).getName()).isEqualTo("메뉴 2");
            assertThat(findMenus.get(2).getName()).isEqualTo("메뉴 3");
            assertThat(findMenus.get(3).getName()).isEqualTo("메뉴 4");
        }

    }

    @Test
    @DisplayName("메뉴 정보 수정")
    void update() throws Exception {
        // given
        Menu menu = Menu.builder()
                .id(1L)
                .name("피자")
                .content("피자 설명")
                .price(20000)
                .build();

        Menu updateParam = Menu.builder()
                .id(1L)
                .name("치킨")
                .content("치킨 설명")
                .price(19000)
                .build();

        MockMultipartFile picture = new MockMultipartFile("picture", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
        given(imageFileHandler.store(any())).willReturn("new_picture.png");
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        menuService.update(updateParam, picture);

        // then
        assertThat(menu.getName()).isEqualTo("치킨");
        assertThat(menu.getContent()).isEqualTo("치킨 설명");
        assertThat(menu.getPrice()).isEqualTo(19000);
        assertThat(menu.getPicture()).isEqualTo("/images/new_picture.png");
    }

    @Nested
    @DisplayName("메뉴 삭제")
    class Delete {

        @Test
        @DisplayName("메뉴 삭제 성공")
        void success() throws Exception {
            // given
            Menu menu = Menu.builder()
                    .id(1L)
                    .name("피자")
                    .content("피자 설명")
                    .picture("picture.png")
                    .price(20000)
                    .build();

            Menu deleteParam = Menu.builder()
                    .id(1L)
                    .build();

            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(imageFileHandler.remove(anyString())).willReturn(true);
            doNothing().when(menuRepository).delete(any());

            // when
            menuService.delete(deleteParam);

            // then
            then(menuRepository).should().delete(menu);
        }

        @Test
        @DisplayName("이미지가 삭제되지 않으면 예외 발생")
        void failPicture() throws Exception {
            // given
            Menu menu = Menu.builder()
                    .id(1L)
                    .name("피자")
                    .content("피자 설명")
                    .picture("picture.png")
                    .price(20000)
                    .build();

            Menu deleteParam = Menu.builder()
                    .id(1L)
                    .build();

            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(imageFileHandler.remove(anyString())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> menuService.delete(deleteParam))
                    .isInstanceOf(FileIOException.class);
        }
    }
}