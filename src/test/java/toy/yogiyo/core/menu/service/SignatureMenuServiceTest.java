package toy.yogiyo.core.menu.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.menu.repository.SignatureMenuRepository;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class SignatureMenuServiceTest {

    @InjectMocks
    SignatureMenuService signatureMenuService;

    @Mock
    SignatureMenuRepository signatureMenuRepository;

    @Test
    @DisplayName("대표 메뉴 추가 - position 1")
    void createPosition1() throws Exception {
        // given
        SignatureMenu signatureMenu = SignatureMenu.builder()
                .id(1L)
                .menu(Menu.builder().id(1L).build())
                .shop(Shop.builder().id(1L).build())
                .build();

        given(signatureMenuRepository.findMaxOrder(anyLong())).willReturn(null);

        // when
        Long createdId = signatureMenuService.create(signatureMenu);

        // then
        assertThat(createdId).isEqualTo(1L);
        assertThat(signatureMenu.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("대표 메뉴 추가 - position 5")
    void createPosition5() throws Exception {
        // given
        SignatureMenu signatureMenu = SignatureMenu.builder()
                .id(1L)
                .menu(Menu.builder().id(1L).build())
                .shop(Shop.builder().id(1L).build())
                .build();

        given(signatureMenuRepository.findMaxOrder(anyLong())).willReturn(4);

        // when
        Long createdId = signatureMenuService.create(signatureMenu);

        // then
        assertThat(createdId).isEqualTo(1L);
        assertThat(signatureMenu.getPosition()).isEqualTo(5);
    }

    @Test
    @DisplayName("대표 메뉴 전체 조회")
    void findAll() throws Exception {
        // given
        Shop shop = Shop.builder().id(1L).build();
        given(signatureMenuRepository.findAlLByShopId(anyLong()))
                .willReturn(Arrays.asList(
                        SignatureMenu.builder().id(1L).shop(shop).position(1).build(),
                        SignatureMenu.builder().id(2L).shop(shop).position(2).build(),
                        SignatureMenu.builder().id(3L).shop(shop).position(3).build(),
                        SignatureMenu.builder().id(4L).shop(shop).position(4).build(),
                        SignatureMenu.builder().id(5L).shop(shop).position(5).build()
                ));

        // when
        List<SignatureMenu> signatureMenus = signatureMenuService.findAll(1L);

        // then
        assertThat(signatureMenus.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("데표 메뉴 전체 삭제")
    void deleteAll() throws Exception {
        // given
        given(signatureMenuRepository.deleteAllByShopId(anyLong())).willReturn(5);

        // when
        int deleteCount = signatureMenuService.deleteAll(1L);

        // then
        assertThat(deleteCount).isEqualTo(5);
    }

}