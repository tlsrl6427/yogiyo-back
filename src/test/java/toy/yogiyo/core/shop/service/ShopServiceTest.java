package toy.yogiyo.core.shop.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import toy.yogiyo.common.exception.AccessDeniedException;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.shop.DeliveryPrice;
import toy.yogiyo.core.shop.Shop;
import toy.yogiyo.core.shop.dto.DeliveryPriceDto;
import toy.yogiyo.core.shop.dto.ShopRegisterRequest;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopUpdateRequest;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
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

    @BeforeAll
    static void beforeAll() {
        new ImageFileUtil().setPath("/image/");
    }

    @Nested
    @DisplayName("가게 입점")
    class Register {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            ShopRegisterRequest registerRequest = getRegisterRequest();

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
            registerRequest.setOrderTypes("가게배달, 포장");
            registerRequest.setPackagingPrice(0);
            registerRequest.setDeliveryPriceDtos(Arrays.asList(
                    new DeliveryPriceDto(10000, 5000),
                    new DeliveryPriceDto(20000, 4000),
                    new DeliveryPriceDto(30000, 3000)));

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
                assertThat(response.getName()).isEqualTo(shop.getName());
                assertThat(response.getIcon()).isEqualTo(shop.getIcon());
                assertThat(response.getBanner()).isEqualTo(shop.getBanner());
                assertThat(response.getOwnerNotice()).isEqualTo(shop.getOwnerNotice());
                assertThat(response.getBusinessHours()).isEqualTo(shop.getBusinessHours());
                assertThat(response.getCallNumber()).isEqualTo(shop.getCallNumber());
                assertThat(response.getAddress()).isEqualTo(shop.getAddress());
                assertThat(response.getDeliveryTime()).isEqualTo(shop.getDeliveryTime());
                assertThat(response.getOrderTypes()).isEqualTo(shop.getOrderTypes());
                assertThat(response.getPackagingPrice()).isEqualTo(shop.getPackagingPrice());

                for (int i = 0; i < response.getDeliveryPriceDtos().size(); i++) {
                    DeliveryPriceDto deliveryPriceDto = response.getDeliveryPriceDtos().get(i);
                    DeliveryPrice deliveryPrice = shop.getDeliveryPrices().get(i);
                    assertThat(deliveryPriceDto.getDeliveryPrice()).isEqualTo(deliveryPrice.getDeliveryPrice());
                    assertThat(deliveryPriceDto.getOrderPrice()).isEqualTo(deliveryPrice.getOrderPrice());
                }
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
                assertThat(shop.getOrderTypes()).isEqualTo(updateRequest.getOrderTypes());
                assertThat(shop.getPackagingPrice()).isEqualTo(updateRequest.getPackagingPrice());

                for (int i = 0; i < shop.getDeliveryPrices().size(); i++) {
                    DeliveryPrice deliveryPrice = shop.getDeliveryPrices().get(i);
                    DeliveryPriceDto deliveryPriceDto = updateRequest.getDeliveryPriceDtos().get(i);
                    assertThat(deliveryPrice.getDeliveryPrice()).isEqualTo(deliveryPriceDto.getDeliveryPrice());
                    assertThat(deliveryPrice.getOrderPrice()).isEqualTo(deliveryPriceDto.getOrderPrice());
                }
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
            updateRequest.setOrderTypes("가게배달, 포장 (수정됨)");
            updateRequest.setPackagingPrice(1000);
            updateRequest.setDeliveryPriceDtos(Arrays.asList(
                    new DeliveryPriceDto(15000, 4500),
                    new DeliveryPriceDto(25000, 3500),
                    new DeliveryPriceDto(35000, 2500)));

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
        Shop shop = new Shop("롯데리아",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "692c0741-f234-448e-ba3f-35b5a394f33d.png",
                "사장님 공지",
                "오전 10시 ~ 오후 10시",
                "010-1234-5678",
                "서울 강남구 영동대로 513",
                30,
                "가게배달, 포장",
                0);

        shop.changeDeliveryPrices(Arrays.asList(
                new DeliveryPrice(10000, 5000),
                new DeliveryPrice(20000, 4000),
                new DeliveryPrice(30000, 3000)));

        return shop;
    }

    private Shop getShopWithOwner(Long ownerId) {
        Shop shop = getShop();
        shop.changeOwner(new Shop.Owner(ownerId));
        return shop;
    }
}