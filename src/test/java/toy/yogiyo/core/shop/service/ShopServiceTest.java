package toy.yogiyo.core.shop.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import toy.yogiyo.common.exception.AccessDeniedException;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.category.dto.CategoryDto;
import toy.yogiyo.core.category.service.CategoryShopService;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.service.OwnerService;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.dto.DeliveryPriceDto;
import toy.yogiyo.core.shop.dto.ShopRegisterRequest;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopUpdateRequest;
import toy.yogiyo.core.shop.repository.ShopRepository;

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

    @Mock
    CategoryShopService categoryShopService;

    @Mock
    OwnerService ownerService;

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
            ShopRegisterRequest registerRequest = givenRegisterRequest();
            MockMultipartFile icon = givenIcon();
            MockMultipartFile banner = givenBanner();

            when(ownerService.findOne(anyLong())).thenReturn(new Owner(1L, "owner", "owner@yogiyo.com", "owner", null));
            when(imageFileHandler.store(icon))
                    .thenReturn("692c0741-f234-448e-ba3f-35b5a394f33d.png");
            when(imageFileHandler.store(banner))
                    .thenReturn("792c0741-f234-448e-ba3f-35b5a394f33d.png");

            doNothing().when(categoryShopService).save(eq(registerRequest.getCategories()), any());

            Shop shop = registerRequest.toEntity(
                    imageFileHandler.store(icon),
                    imageFileHandler.store(banner));

            when(shopRepository.save(any())).thenReturn(shop);

            // when
            Long createdId = shopService.register(registerRequest, icon, banner, anyLong());

            // then
            assertThat(createdId).isEqualTo(shop.getId());
        }

        @Test
        @DisplayName("가게명 중복이면 예외 발생")
        void registerFailDuplicateName() throws Exception {
            // given
            ShopRegisterRequest registerRequest = givenRegisterRequest();
            MockMultipartFile icon = givenIcon();
            MockMultipartFile banner = givenBanner();
            when(shopRepository.existsByName(registerRequest.getName()))
                    .thenReturn(true);

            // when & then
            assertThatThrownBy(() -> shopService.register(registerRequest, icon, banner, anyLong()))
                    .isInstanceOf(EntityExistsException.class);
        }


        private MockMultipartFile givenIcon() throws IOException {
            return new MockMultipartFile("icon", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
        }

        private MockMultipartFile givenBanner() throws IOException {
            return new MockMultipartFile("banner", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
        }


        private ShopRegisterRequest givenRegisterRequest() throws IOException {

            ShopRegisterRequest registerRequest = new ShopRegisterRequest();
            registerRequest.setName("롯데리아");
            registerRequest.setOwnerNotice("사장님 공지");
            registerRequest.setBusinessHours("오전 10시 ~ 오후 10시");
            registerRequest.setCallNumber("010-1234-5678");
            registerRequest.setAddress("서울 강남구 영동대로 513");
            registerRequest.setDeliveryTime(30);
            registerRequest.setOrderTypes("가게배달, 포장");
            registerRequest.setPackagingPrice(0);
            registerRequest.setDeliveryPrices(Arrays.asList(
                    new DeliveryPriceDto(10000, 5000),
                    new DeliveryPriceDto(20000, 4000),
                    new DeliveryPriceDto(30000, 3000)));
            registerRequest.setCategories(Arrays.asList(
                    new CategoryDto(1L, "치킨", "picture.png"),
                    new CategoryDto(2L, "피자", "picture.png"),
                    new CategoryDto(3L, "분식", "picture.png")));

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
                assertThat(response.getOwnerNotice()).isEqualTo(shop.getOwnerNotice());
                assertThat(response.getBusinessHours()).isEqualTo(shop.getBusinessHours());
                assertThat(response.getCallNumber()).isEqualTo(shop.getCallNumber());
                assertThat(response.getAddress()).isEqualTo(shop.getAddress());
                assertThat(response.getDeliveryTime()).isEqualTo(shop.getDeliveryTime());
                assertThat(response.getOrderTypes()).isEqualTo(shop.getOrderTypes());
                assertThat(response.getPackagingPrice()).isEqualTo(shop.getPackagingPrice());

                for (int i = 0; i < response.getDeliveryPrices().size(); i++) {
                    DeliveryPriceDto deliveryPriceDto = response.getDeliveryPrices().get(i);
                    DeliveryPriceInfo deliveryPriceInfo = shop.getDeliveryPriceInfos().get(i);
                    assertThat(deliveryPriceDto.getDeliveryPrice()).isEqualTo(deliveryPriceInfo.getDeliveryPrice());
                    assertThat(deliveryPriceDto.getOrderPrice()).isEqualTo(deliveryPriceInfo.getOrderPrice());
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
                doNothing().when(categoryShopService).changeCategory(updateRequest.getCategories(), shop);

                // when
                shopService.updateInfo(shop.getId(), 1L, updateRequest);

                // then
                assertThat(shop.getName()).isEqualTo(updateRequest.getName());
                assertThat(shop.getOwnerNotice()).isEqualTo(updateRequest.getOwnerNotice());
                assertThat(shop.getBusinessHours()).isEqualTo(updateRequest.getBusinessHours());
                assertThat(shop.getCallNumber()).isEqualTo(updateRequest.getCallNumber());
                assertThat(shop.getAddress()).isEqualTo(updateRequest.getAddress());
                assertThat(shop.getDeliveryTime()).isEqualTo(updateRequest.getDeliveryTime());
                assertThat(shop.getOrderTypes()).isEqualTo(updateRequest.getOrderTypes());
                assertThat(shop.getPackagingPrice()).isEqualTo(updateRequest.getPackagingPrice());

                for (int i = 0; i < shop.getDeliveryPriceInfos().size(); i++) {
                    DeliveryPriceInfo deliveryPriceInfo = shop.getDeliveryPriceInfos().get(i);
                    DeliveryPriceDto deliveryPriceDto = updateRequest.getDeliveryPrices().get(i);
                    assertThat(deliveryPriceInfo.getDeliveryPrice()).isEqualTo(deliveryPriceDto.getDeliveryPrice());
                    assertThat(deliveryPriceInfo.getOrderPrice()).isEqualTo(deliveryPriceDto.getOrderPrice());
                }

                verify(categoryShopService).changeCategory(updateRequest.getCategories(), shop);
            }

            @Test
            @DisplayName("가게가 없으면 예외 발생")
            void failNotFound() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                ShopUpdateRequest updateRequest = getUpdateRequest();
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> shopService.updateInfo(shop.getId(), shop.getOwner().getId(), updateRequest))
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
                assertThatThrownBy(() -> shopService.updateInfo(shop.getId(), 2L, updateRequest))
                        .isInstanceOf(AccessDeniedException.class);

                // then
                verify(shopRepository).findById(shop.getId());
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
                updateRequest.setDeliveryPrices(Arrays.asList(
                        new DeliveryPriceDto(15000, 4500),
                        new DeliveryPriceDto(25000, 3500),
                        new DeliveryPriceDto(35000, 2500)));
                updateRequest.setCategories(Arrays.asList(
                        new CategoryDto(1L, "치킨", "picture.png"),
                        new CategoryDto(2L, "피자", "picture.png"),
                        new CategoryDto(3L, "분식", "picture.png")));

                return updateRequest;
            }

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
            verify(shopRepository).findById(shop.getId());
            verify(shopRepository).delete(shop);
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
            verify(shopRepository).findById(shop.getId());
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
            verify(imageFileHandler).remove(anyString());
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
                new DeliveryPriceInfo(10000, 5000),
                new DeliveryPriceInfo(20000, 4000),
                new DeliveryPriceInfo(30000, 3000)));

        return shop;
    }

    private Shop getShopWithOwner(Long ownerId) {
        Shop shop = getShop();
        shop.changeOwner(new Owner(ownerId, "owner", "owner@yogiyo.com", "owner", null));
        return shop;
    }
}