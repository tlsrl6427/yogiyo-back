package toy.yogiyo.core.menuoption.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.repository.MenuOptionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class MenuOptionServiceTest {

    @InjectMocks
    MenuOptionService menuOptionService;

    @Mock
    MenuOptionRepository menuOptionRepository;

    @Test
    @DisplayName("옵션 추가")
    void add() throws Exception {
        // given
        given(menuOptionRepository.findMaxOrder(anyLong())).willReturn(null);
        MenuOption menuOption = MenuOption.builder()
                .id(1L)
                .menuOptionGroup(MenuOptionGroup.builder().id(1L).build())
                .content("콜라 500ml 변경")
                .price(500)
                .build();

        // when
        Long addedId = menuOptionService.create(menuOption);

        // then
        assertThat(addedId).isEqualTo(1L);
    }

    @Test
    @DisplayName("옵션 조회")
    void find() throws Exception {
        // given
        MenuOption menuOption = MenuOption.builder()
                .id(1L)
                .content("콜라 500ml 변경")
                .price(500)
                .build();

        given(menuOptionRepository.findById(anyLong())).willReturn(Optional.of(menuOption));

        // when
        MenuOption findMenuOption = menuOptionService.get(1L);

        // then
        assertThat(findMenuOption).isEqualTo(menuOption);
    }

    @Test
    @DisplayName("옵션 내용 수정")
    void update() throws Exception {
        // given
        MenuOption menuOption = MenuOption.builder()
                .id(1L)
                .content("콜라 500ml 변경")
                .price(500)
                .build();

        given(menuOptionRepository.findById(anyLong())).willReturn(Optional.of(menuOption));

        MenuOption updateParam = MenuOption.builder()
                .id(1L)
                .content("콜라 1L 변경")
                .price(1000)
                .build();

        // when
        menuOptionService.update(updateParam);

        // then
        assertThat(menuOption.getContent()).isEqualTo("콜라 1L 변경");
        assertThat(menuOption.getPrice()).isEqualTo(1000);
    }

    @Test
    @DisplayName("옵션 삭제")
    void delete() throws Exception {
        // given
        MenuOption menuOption = MenuOption.builder()
                .id(1L)
                .content("콜라 500ml 변경")
                .price(500)
                .build();

        given(menuOptionRepository.findById(anyLong())).willReturn(Optional.of(menuOption));
        doNothing().when(menuOptionRepository).delete(any());

        // when
        menuOptionService.delete(1L);

        // then
        then(menuOptionRepository).should().delete(menuOption);
    }

    @Test
    @DisplayName("옵션 그룹 내 옵션 전체 삭제")
    void deleteAll() throws Exception {
        // given
        given(menuOptionRepository.deleteAllByGroupId(anyLong())).willReturn(5);

        // when
        int deleteCount = menuOptionService.deleteAll(1L);

        // then
        assertThat(deleteCount).isEqualTo(5);
    }

    @Test
    @DisplayName("옵션 정렬 순서 변경")
    void changeOrder() throws Exception {
        // given
        List<MenuOption> menuOptions = Arrays.asList(
                MenuOption.builder().id(1L).build(),
                MenuOption.builder().id(2L).build(),
                MenuOption.builder().id(3L).build(),
                MenuOption.builder().id(4L).build(),
                MenuOption.builder().id(5L).build()
        );
        given(menuOptionRepository.findAllByMenuOptionGroupId(anyLong())).willReturn(menuOptions);

        List<MenuOption> params = Arrays.asList(
                MenuOption.builder().id(5L).build(),
                MenuOption.builder().id(4L).build(),
                MenuOption.builder().id(3L).build(),
                MenuOption.builder().id(2L).build(),
                MenuOption.builder().id(1L).build()
        );

        // when
        menuOptionService.updatePosition(1L, params);

        // then
        assertThat(menuOptions.get(0).getPosition()).isEqualTo(5);
        assertThat(menuOptions.get(1).getPosition()).isEqualTo(4);
        assertThat(menuOptions.get(2).getPosition()).isEqualTo(3);
        assertThat(menuOptions.get(3).getPosition()).isEqualTo(2);
        assertThat(menuOptions.get(4).getPosition()).isEqualTo(1);
    }
}