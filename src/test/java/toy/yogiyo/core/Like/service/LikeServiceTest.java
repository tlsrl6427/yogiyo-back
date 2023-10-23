package toy.yogiyo.core.Like.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.core.Like.domain.Like;
import toy.yogiyo.core.Like.dto.LikeScrollResponse;
import toy.yogiyo.core.Like.repository.LikeRepository;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    LikeService likeService;

    @Mock
    LikeRepository likeRepository;

    @Mock
    ShopRepository shopRepository;

    Like like;
    Member member;
    Shop shop;

    @BeforeEach
    void beforeEach(){
        like = Like.builder()
                .id(1L)
                .build();

        member = Member.builder()
                .id(1L)
                .build();

        shop = Shop.builder()
                .id(1L)
                .build();
    }

    @DisplayName("찜하기")
    @Test
    void toggleLike_like() {
        given(likeRepository.findByMemberAndShop(any(), any())).willReturn(Optional.empty());
        given(shopRepository.findById(any())).willReturn(Optional.of(shop));

        likeService.toggleLike(member, shop.getId());

        assertAll(
                () -> verify(likeRepository).findByMemberAndShop(any(), any()),
                () -> verify(shopRepository).findById(any()),
                () -> verify(likeRepository).save(any())
        );
    }

    @DisplayName("찜 취소")
    @Test
    void toggleLike_cancel() {
        given(likeRepository.findByMemberAndShop(any(), any())).willReturn(Optional.ofNullable(like));

        likeService.toggleLike(member, shop.getId());

        assertAll(
                () -> verify(likeRepository).findByMemberAndShop(any(), any()),
                () -> verify(likeRepository).delete(any())
        );
    }

    @Test
    void getLikes() {
        List<Shop> shops = new ArrayList<>();
        for(int i=5; i>0; i--){
            Shop shop1 = Shop.builder()
                    .id((long) i)
                    .build();
            shops.add(shop1);
        }

        given(shopRepository.scrollLikes(any(), any())).willReturn(shops);
        LikeScrollResponse likes = likeService.getLikes(member, shop.getId());

        assertAll(
                () -> verify(shopRepository).scrollLikes(any(), any()),
                () -> assertThat(likes.isHasNext()).isFalse(),
                () -> assertThat(likes.getLastId()).isEqualTo(1L),
                () -> assertThat(likes.getLikeResponses().size()).isEqualTo(5)
        );
    }
}