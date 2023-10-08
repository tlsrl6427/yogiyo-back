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
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.service.CategoryShopService;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.service.OwnerService;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.dto.*;
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

            doNothing().when(categoryShopService).save(anyList(), any());

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
            registerRequest.setCallNumber("010-1234-5678");
            registerRequest.setAddress("서울 강남구 영동대로 513");
            registerRequest.setCategoryIds(Arrays.asList(1L, 2L, 3L));

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
            @DisplayName("가게 정보 조회")
            void getInfo() throws Exception {
                // given
                Shop shop = getShop();

                when(shopRepository.findById(shop.getId()))
                        .thenReturn(Optional.of(shop));

                // when
                ShopInfoResponse response = shopService.getInfo(shop.getId());

                // then
                assertThat(response.getName()).isEqualTo(shop.getName());
                assertThat(response.getCallNumber()).isEqualTo(shop.getCallNumber());
                assertThat(response.getAddress()).isEqualTo(shop.getAddress());
                for (int i = 0; i < response.getCategories().size(); i++) {
                    String categoryName = response.getCategories().get(i);
                    assertThat(categoryName).isEqualTo(shop.getCategoryShop().get(i).getCategory().getName());
                }
            }

            @Test
            @DisplayName("사장님 공지 조회")
            void getNotice() throws Exception {
                // given
                Shop shop = getShop();

                when(shopRepository.findById(shop.getId()))
                        .thenReturn(Optional.of(shop));

                // when
                ShopNoticeResponse response = shopService.getNotice(shop.getId());

                // then
                assertThat(response.getNotice()).isEqualTo(shop.getOwnerNotice());
            }

            @Test
            @DisplayName("영업 시간 조회")
            void getBusinessHours() throws Exception {
                // given
                Shop shop = getShop();

                when(shopRepository.findById(shop.getId()))
                        .thenReturn(Optional.of(shop));

                // when
                ShopBusinessHourResponse response = shopService.getBusinessHours(shop.getId());

                // then
                assertThat(response.getBusinessHours()).isEqualTo(shop.getBusinessHours());
            }

            @Test
            @DisplayName("배달 요금 조회")
            void getDeliveryPrice() throws Exception {
                // given
                Shop shop = getShop();

                when(shopRepository.findById(shop.getId()))
                        .thenReturn(Optional.of(shop));

                // when
                ShopDeliveryPriceResponse response = shopService.getDeliveryPrice(shop.getId());

                // then
                for (int i = 0; i < response.getDeliveryPrices().size(); i++) {
                    DeliveryPriceDto deliveryPriceDto = response.getDeliveryPrices().get(i);
                    DeliveryPriceInfo deliveryPriceInfo = shop.getDeliveryPriceInfos().get(i);
                    assertThat(deliveryPriceDto.getDeliveryPrice()).isEqualTo(deliveryPriceInfo.getDeliveryPrice());
                    assertThat(deliveryPriceDto.getOrderPrice()).isEqualTo(deliveryPriceInfo.getOrderPrice());
                }
            }

        }

        @Nested
        @DisplayName("수정")
        class Update {

            @Test
            @DisplayName("가게 정보 수정")
            void updateInfo() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
                ShopUpdateRequest updateRequest = getUpdateRequest();
                doNothing().when(categoryShopService).changeCategory(anyList(), any());

                // when
                shopService.updateShopInfo(shop.getId(), 1L, updateRequest);

                // then
                assertThat(shop.getName()).isEqualTo(updateRequest.getName());
                assertThat(shop.getCallNumber()).isEqualTo(updateRequest.getCallNumber());
                assertThat(shop.getAddress()).isEqualTo(updateRequest.getAddress());
                verify(categoryShopService).changeCategory(updateRequest.getCategoryIds(), shop);
            }
            
            @Test
            @DisplayName("사장님 공지 수정")
            void updateNotice() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
                ShopNoticeUpdateRequest request = getNoticeUpdateRequest();

                // when
                shopService.updateNotice(shop.getId(), 1L, request);

                // then
                assertThat(shop.getOwnerNotice()).isEqualTo(request.getNotice());
            }
            
            @Test
            @DisplayName("영업 시간 수정")
            void updateBusinessHours() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
                ShopBusinessHourUpdateRequest request = getBusinessHoursUpdateRequest();

                // when
                shopService.updateBusinessHours(shop.getId(), 1L, request);

                // then
                assertThat(shop.getBusinessHours()).isEqualTo(request.getBusinessHours());
            }
            
            @Test
            @DisplayName("배달 요금 수정")
            void updateDeliveryPrice() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
                DeliveryPriceUpdateRequest request = getDeliveryPriceUpdateRequest();

                // when
                shopService.updateDeliveryPrice(shop.getId(), 1L, request);

                // then
                for (int i = 0; i < shop.getDeliveryPriceInfos().size(); i++) {
                    DeliveryPriceInfo deliveryPriceInfo = shop.getDeliveryPriceInfos().get(i);
                    DeliveryPriceDto deliveryPriceDto = request.getDeliveryPrices().get(i);
                    assertThat(deliveryPriceInfo.getDeliveryPrice()).isEqualTo(deliveryPriceDto.getDeliveryPrice());
                    assertThat(deliveryPriceInfo.getOrderPrice()).isEqualTo(deliveryPriceDto.getOrderPrice());
                }
            }

            private ShopUpdateRequest getUpdateRequest() {
                ShopUpdateRequest updateRequest = new ShopUpdateRequest();
                updateRequest.setName("롯데리아 (수정됨)");
                updateRequest.setCallNumber("010-1234-5678 (수정됨)");
                updateRequest.setAddress("서울 강남구 영동대로 513 (수정됨)");
                updateRequest.setCategoryIds(Arrays.asList(1L, 2L, 3L));

                return updateRequest;
            }

            private ShopNoticeUpdateRequest getNoticeUpdateRequest() {
                return ShopNoticeUpdateRequest.builder()
                        .notice("사장님 공지 (수정됨)")
                        .build();
            }

            private ShopBusinessHourUpdateRequest getBusinessHoursUpdateRequest() {
                return ShopBusinessHourUpdateRequest.builder()
                        .businessHours("오전 10시 ~ 오후 10시 (수정됨)")
                        .build();
            }

            private DeliveryPriceUpdateRequest getDeliveryPriceUpdateRequest() {
                return DeliveryPriceUpdateRequest.builder()
                        .deliveryPrices(Arrays.asList(
                                new DeliveryPriceDto(15000, 4500),
                                new DeliveryPriceDto(25000, 3500),
                                new DeliveryPriceDto(35000, 2500)))
                        .build();
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
        Shop shop = Shop.builder()
                .name("롯데리아")
                .icon("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .banner("692c0741-f234-448e-ba3f-35b5a394f33d.png")
                .ownerNotice("사장님 공지")
                .businessHours("오전 10시 ~ 오후 10시")
                .callNumber("010-1234-5678")
                .address("서울 강남구 영동대로 513")
                .deliveryTime(30)
                .categoryShop(Arrays.asList(
                        CategoryShop.builder().category(Category.builder().name("카테고리1").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리2").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리3").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리4").build()).build()
                ))
                .build();

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