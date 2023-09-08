package toy.yogiyo.core.shop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import toy.yogiyo.common.exception.AccessDeniedException;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.core.shop.Shop;
import toy.yogiyo.core.shop.dto.ShopRegisterRequest;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopUpdateRequest;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @InjectMocks
    ShopService shopService;

    @Mock
    ShopRepository shopRepository;

    @Mock
    ImageFileHandler imageFileHandler;

    @Mock
    Environment env;

    @Nested
    @DisplayName("가게 입점")
    class Register {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            ShopRegisterRequest registerRequest = getRegisterRequest();

            when(env.getProperty("image.path")).thenReturn("images");
            when(imageFileHandler.store(registerRequest.getIcon()))
                    .thenReturn("692c0741-f234-448e-ba3f-35b5a394f33d.png");
            when(imageFileHandler.store(registerRequest.getBanner()))
                    .thenReturn("792c0741-f234-448e-ba3f-35b5a394f33d.png");


            Shop shop = registerRequest.toEntity(
                    imageFileHandler.store(registerRequest.getIcon()),
                    imageFileHandler.store(registerRequest.getBanner()));

            when(shopRepository.save(any())).thenReturn(shop);

            // when
            Long createdId = shopService.register(registerRequest);

            // then
            assertThat(createdId).isEqualTo(shop.getId());
        }

        @Test
        @DisplayName("가게명 중복이면 예외 발생")
        void registerFailDuplicateName() throws Exception {
            // given
            ShopRegisterRequest registerRequest = getRegisterRequest();
            when(shopRepository.existsByName(registerRequest.getName()))
                    .thenReturn(true);

            // when & then
            assertThatThrownBy(() -> shopService.register(registerRequest))
                    .isInstanceOf(EntityExistsException.class);
        }

        private ShopRegisterRequest getRegisterRequest() throws IOException {
            MockMultipartFile icon = new MockMultipartFile("picture", "images.png", "png", new FileInputStream("D:\\workspace\\personal\\store\\test-resources\\images.png"));
            MockMultipartFile banner = new MockMultipartFile("picture", "images.png", "png", new FileInputStream("D:\\workspace\\personal\\store\\test-resources\\images.png"));

            ShopRegisterRequest registerRequest = new ShopRegisterRequest();
            registerRequest.setName("롯데리아");
            registerRequest.setIcon(icon);
            registerRequest.setBanner(banner);
            registerRequest.setOwnerNotice("사장님 공지");
            registerRequest.setBusinessHours("오전 10시 ~ 오후 10시");
            registerRequest.setCallNumber("010-1234-5678");
            registerRequest.setAddress("서울 강남구 영동대로 513");
            registerRequest.setDeliveryTime(30);
            registerRequest.setLeastOrderPrice(15000);
            registerRequest.setOrderTypes("가게배달, 포장");
            registerRequest.setDeliveryPrice(3000);
            registerRequest.setPackagingPrice(0);

            return registerRequest;
        }

    }

    @Nested
    @DisplayName("가게/원산지 정보")
    class Detail {

        @Nested
        @DisplayName("조회")
        class Get {

            @Test
            @DisplayName("성공")
            void success() throws Exception {
                // given
                Shop shop = getShop();

                when(shopRepository.findById(shop.getId()))
                        .thenReturn(Optional.of(shop));

                // when
                ShopDetailsResponse response = shopService.getDetailInfo(shop.getId());

                // then
                assertThat(response.getName()).isEqualTo("롯데리아");
                assertThat(response.getIcon()).isEqualTo("/images/692c0741-f234-448e-ba3f-35b5a394f33d.png");
                assertThat(response.getBanner()).isEqualTo("/images/692c0741-f234-448e-ba3f-35b5a394f33d.png");
                assertThat(response.getOwnerNotice()).isEqualTo("사장님 공지");
                assertThat(response.getBusinessHours()).isEqualTo("오전 10시 ~ 오후 10시");
                assertThat(response.getCallNumber()).isEqualTo("010-1234-5678");
                assertThat(response.getAddress()).isEqualTo("서울 강남구 영동대로 513");
                assertThat(response.getDeliveryTime()).isEqualTo(30);
                assertThat(response.getLeastOrderPrice()).isEqualTo(15000);
                assertThat(response.getOrderTypes()).isEqualTo("가게배달, 포장");
                assertThat(response.getDeliveryPrice()).isEqualTo(3000);
                assertThat(response.getPackagingPrice()).isEqualTo(0);
            }

            @Test
            @DisplayName("실패")
            void fail() throws Exception {
                // given
                Shop shop = getShop();
                when(shopRepository.findById(shop.getId()))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> shopService.getDetailInfo(shop.getId()))
                        .isInstanceOf(EntityNotFoundException.class);
            }

        }

        @Nested
        @DisplayName("수정")
        class Update {

            @Test
            @DisplayName("성공")
            void updateShopInfo() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
                ShopUpdateRequest updateRequest = getUpdateRequest();

                // when
                shopService.updateInfo(updateRequest, 1L);

                // then
                assertThat(shop.getName()).isEqualTo(updateRequest.getName());
                assertThat(shop.getOwnerNotice()).isEqualTo(updateRequest.getOwnerNotice());
                assertThat(shop.getBusinessHours()).isEqualTo(updateRequest.getBusinessHours());
                assertThat(shop.getCallNumber()).isEqualTo(updateRequest.getCallNumber());
                assertThat(shop.getAddress()).isEqualTo(updateRequest.getAddress());
                assertThat(shop.getDeliveryTime()).isEqualTo(updateRequest.getDeliveryTime());
                assertThat(shop.getLeastOrderPrice()).isEqualTo(updateRequest.getLeastOrderPrice());
                assertThat(shop.getOrderTypes()).isEqualTo(updateRequest.getOrderTypes());
                assertThat(shop.getDeliveryPrice()).isEqualTo(updateRequest.getDeliveryPrice());
                assertThat(shop.getPackagingPrice()).isEqualTo(updateRequest.getPackagingPrice());
            }

            @Test
            @DisplayName("가게가 없으면 예외 발생")
            void failNotFound() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                ShopUpdateRequest updateRequest = getUpdateRequest();
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> shopService.updateInfo(updateRequest, shop.getOwner().getId()))
                        .isInstanceOf(EntityNotFoundException.class);
            }

            @Test
            @DisplayName("Owner가 다르면 예외 발생")
            void updateShopInfoFailDiffOwner() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
                ShopUpdateRequest updateRequest = getUpdateRequest();

                // when & then
                assertThatThrownBy(() -> shopService.updateInfo(updateRequest, 2L))
                        .isInstanceOf(AccessDeniedException.class);

                // then
                verify(shopRepository, times(1)).findById(shop.getId());
            }

        }

        private ShopUpdateRequest getUpdateRequest() {
            ShopUpdateRequest updateRequest = new ShopUpdateRequest();
            updateRequest.setName("롯데리아 (수정됨)");
            updateRequest.setOwnerNotice("사장님 공지 (수정됨)");
            updateRequest.setBusinessHours("오전 10시 ~ 오후 10시 (수정됨)");
            updateRequest.setCallNumber("010-1234-5678 (수정됨)");
            updateRequest.setAddress("서울 강남구 영동대로 513 (수정됨)");
            updateRequest.setDeliveryTime(60);
            updateRequest.setLeastOrderPrice(17000);
            updateRequest.setOrderTypes("가게배달, 포장 (수정됨)");
            updateRequest.setDeliveryPrice(4000);
            updateRequest.setPackagingPrice(1000);

            return updateRequest;
        }

    }

    @Nested
    @DisplayName("가게 삭제")
    class Delete {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Shop shop = getShopWithOwner(1L);
            when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
            when(imageFileHandler.remove(anyString())).thenReturn(true);
            doNothing().when(shopRepository).delete(shop);

            // when
            shopService.delete(shop.getId(), 1L);

            // then
            verify(imageFileHandler, times(2)).remove(anyString());
            verify(shopRepository, times(1)).findById(shop.getId());
            verify(shopRepository, times(1)).delete(shop);
        }

        @Test
        @DisplayName("Owner가 다르면 예외 발생")
        void failDiffOwner() throws Exception {
            // given
            Shop shop = getShopWithOwner(1L);
            when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

            // when
            assertThatThrownBy(() -> shopService.delete(shop.getId(), 2L))
                    .isInstanceOf(AccessDeniedException.class);

            // then
            verify(shopRepository, times(1)).findById(shop.getId());
        }

        @Test
        @DisplayName("Icon, Banner 삭제 안되면 에러 발생")
        void failIconBanner() throws Exception {
            // given
            Shop shop = getShopWithOwner(1L);
            when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
            when(imageFileHandler.remove(anyString())).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> shopService.delete(shop.getId(), 1L))
                    .isInstanceOf(FileIOException.class);

            // then
            verify(imageFileHandler, times(1)).remove(anyString());
        }

        @Test
        @DisplayName("가게가 없으면 예외 발생")
        void failNotFound() throws Exception {
            // given
            Shop shop = getShopWithOwner(1L);
            when(shopRepository.findById(shop.getId())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> shopService.delete(getShop().getId(), shop.getOwner().getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    private Shop getShop() {
        return new Shop("롯데리아",
                "/images/692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "/images/692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후 10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                15000,
                "가게배달, 포장",
                3000,
                0);
    }

    private Shop getShopWithOwner(Long ownerId) {
        Shop shop = getShop();
        shop.changeOwner(new Shop.Owner(ownerId));
        return shop;
    }
}