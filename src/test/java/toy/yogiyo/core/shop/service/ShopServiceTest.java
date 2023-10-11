package toy.yogiyo.core.shop.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.exception.AccessDeniedException;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.FileIOException;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.Member.domain.ProviderType;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.service.CategoryShopService;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.dto.*;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

            when(imageFileHandler.store(icon))
                    .thenReturn("692c0741-f234-448e-ba3f-35b5a394f33d.png");
            when(imageFileHandler.store(banner))
                    .thenReturn("792c0741-f234-448e-ba3f-35b5a394f33d.png");

            doNothing().when(categoryShopService).save(anyList(), any());

            when(shopRepository.save(any())).thenReturn(any());

            // when
            Long createdId = shopService.register(registerRequest, icon, banner, Owner.builder().build());

            // then
            verify(shopRepository).save(any());
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
            assertThatThrownBy(() -> shopService.register(registerRequest, icon, banner, Owner.builder().build()))
                    .isInstanceOf(EntityExistsException.class);
        }


        private MockMultipartFile givenIcon() throws IOException {
            return new MockMultipartFile("icon", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
        }

        private MockMultipartFile givenBanner() throws IOException {
            return new MockMultipartFile("banner", "images.png", MediaType.IMAGE_PNG_VALUE, "<<image png>>".getBytes());
        }


        private ShopRegisterRequest givenRegisterRequest() throws IOException {
            return ShopRegisterRequest.builder()
                    .name("롯데리아")
                    .callNumber("010-1234-5678")
                    .address("서울 강남구 영동대로 513")
                    .latitude(36.674648)
                    .longitude(127.448544)
                    .categoryIds(Arrays.asList(1L, 2L, 3L))
                    .build();
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
            @DisplayName("가게 전화번호 수정")
            void updateCallNumber() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
                ShopUpdateCallNumberRequest updateRequest = ShopUpdateCallNumberRequest.builder()
                        .callNumber("010-1234-1234")
                        .build();

                // when
                shopService.updateCallNumber(shop.getId(), shop.getOwner(), updateRequest);

                // then
                assertThat(shop.getCallNumber()).isEqualTo("010-1234-1234");
            }
            
            @Test
            @DisplayName("사장님 공지 수정")
            void updateNotice() throws Exception {
                // given
                Shop shop = getShopWithOwner(1L);
                when(imageFileHandler.store(any())).thenReturn("image.png");
                when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
                ShopNoticeUpdateRequest request = ShopNoticeUpdateRequest.builder()
                        .title("공지 제목")
                        .notice("사장님 공지")
                        .build();

                List<MultipartFile> imageFiles = List.of(
                        new MockMultipartFile("images", "image1.png", MediaType.IMAGE_PNG_VALUE, "<<image.png>>".getBytes()),
                        new MockMultipartFile("images", "image2.png", MediaType.IMAGE_PNG_VALUE, "<<image.png>>".getBytes()),
                        new MockMultipartFile("images", "image3.png", MediaType.IMAGE_PNG_VALUE, "<<image.png>>".getBytes())
                );

                // when
                shopService.updateNotice(shop.getId(), shop.getOwner(), request, imageFiles);

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
                shopService.updateBusinessHours(shop.getId(), shop.getOwner(), request);

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
                shopService.updateDeliveryPrice(shop.getId(), shop.getOwner(), request);

                // then
                for (int i = 0; i < shop.getDeliveryPriceInfos().size(); i++) {
                    DeliveryPriceInfo deliveryPriceInfo = shop.getDeliveryPriceInfos().get(i);
                    DeliveryPriceDto deliveryPriceDto = request.getDeliveryPrices().get(i);
                    assertThat(deliveryPriceInfo.getDeliveryPrice()).isEqualTo(deliveryPriceDto.getDeliveryPrice());
                    assertThat(deliveryPriceInfo.getOrderPrice()).isEqualTo(deliveryPriceDto.getOrderPrice());
                }
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
            shopService.delete(shop.getId(), shop.getOwner());

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
            assertThatThrownBy(() -> shopService.delete(shop.getId(), Owner.builder().id(2L).build()))
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
            assertThatThrownBy(() -> shopService.delete(shop.getId(), shop.getOwner()))
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
            assertThatThrownBy(() -> shopService.delete(getShop().getId(), shop.getOwner()))
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
        shop.changeOwner(new Owner(ownerId, "owner", "owner@yogiyo.com", "owner", ProviderType.DEFAULT));
        return shop;
    }
}