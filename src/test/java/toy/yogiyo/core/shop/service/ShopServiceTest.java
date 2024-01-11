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
import toy.yogiyo.core.deliveryplace.domain.DeliveryPriceInfo;
import toy.yogiyo.core.deliveryplace.dto.DeliveryPriceDto;
import toy.yogiyo.core.deliveryplace.dto.DeliveryPriceUpdateRequest;
import toy.yogiyo.core.deliveryplace.dto.DeliveryPriceResponse;
import toy.yogiyo.core.member.domain.ProviderType;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.service.CategoryService;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.*;
import toy.yogiyo.core.shop.dto.*;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.io.IOException;
import java.time.LocalTime;
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
    CategoryService categoryService;


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

            when(categoryService.getCategory(anyString())).thenReturn(Category.builder().build());

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
                    .categories(Arrays.asList("치킨", "한식", "중국집"))
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
                Shop shop = Shop.builder()
                        .id(1L)
                        .businessHours(List.of(
                                BusinessHours.builder()
                                        .dayOfWeek(Days.EVERYDAY)
                                        .isOpen(true)
                                        .openTime(LocalTime.of(10, 0))
                                        .closeTime(LocalTime.of(20, 0))
                                        .build()
                        ))
                        .build();

                when(shopRepository.findById(anyLong()))
                        .thenReturn(Optional.of(shop));

                // when
                ShopBusinessHourResponse response = shopService.getBusinessHours(shop.getId());

                // then
                assertThat(response.getBusinessHours().get(0).getDayOfWeek()).isEqualTo(Days.EVERYDAY);
                assertThat(response.getBusinessHours().get(0).getOpenTime()).isEqualTo(LocalTime.of(10, 0));
                assertThat(response.getBusinessHours().get(0).getCloseTime()).isEqualTo(LocalTime.of(20, 0));
            }

            @Test
            @DisplayName("휴뮤일 조회")
            void getCloseDays() throws Exception {
                // given
                Shop shop = Shop.builder()
                        .id(1L)
                        .closeDays(List.of(
                                CloseDay.builder().weekNumOfMonth(1).dayOfWeek(Days.MONDAY).build(),
                                CloseDay.builder().weekNumOfMonth(3).dayOfWeek(Days.MONDAY).build()
                        ))
                        .build();
                when(shopRepository.findById(anyLong())).thenReturn(Optional.of(shop));

                // when
                ShopCloseDayResponse response = shopService.getCloseDays(1L);

                // then
                assertThat(response.getCloseDays().get(0).getWeekNumOfMonth()).isEqualTo(1);
                assertThat(response.getCloseDays().get(0).getDayOfWeek()).isEqualTo(Days.MONDAY);
                assertThat(response.getCloseDays().get(1).getWeekNumOfMonth()).isEqualTo(3);
                assertThat(response.getCloseDays().get(1).getDayOfWeek()).isEqualTo(Days.MONDAY);
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
                Shop shop = Shop.builder()
                        .id(1L)
                        .owner(Owner.builder().id(1L).build())
                        .build();
                when(shopRepository.findById(anyLong())).thenReturn(Optional.of(shop));
                ShopBusinessHourUpdateRequest request = ShopBusinessHourUpdateRequest.builder()
                        .businessHours(List.of(
                                new ShopBusinessHourUpdateRequest.BusinessHoursDto(Days.MONDAY, true, LocalTime.of(10, 0), LocalTime.of(20, 0), null, null),
                                new ShopBusinessHourUpdateRequest.BusinessHoursDto(Days.THURSDAY, true, LocalTime.of(10, 0), LocalTime.of(20, 0), null, null),
                                new ShopBusinessHourUpdateRequest.BusinessHoursDto(Days.FRIDAY, true, LocalTime.of(10, 0), LocalTime.of(20, 0), null, null)
                        ))
                        .build();

                // when
                shopService.updateBusinessHours(shop.getId(), shop.getOwner(), request);

                // then
                assertThat(shop.getBusinessHours().get(0).getDayOfWeek()).isEqualTo(Days.MONDAY);
                assertThat(shop.getBusinessHours().get(1).getDayOfWeek()).isEqualTo(Days.THURSDAY);
                assertThat(shop.getBusinessHours().get(2).getDayOfWeek()).isEqualTo(Days.FRIDAY);
            }

            @Test
            @DisplayName("휴무일 수정")
            void updateCloseDays() throws Exception {
                // given
                Shop shop = Shop.builder()
                        .id(1L)
                        .owner(Owner.builder().id(1L).build())
                        .build();
                when(shopRepository.findById(anyLong())).thenReturn(Optional.of(shop));
                ShopCloseDayUpdateRequest request = ShopCloseDayUpdateRequest.builder()
                        .closeDays(List.of(
                                new ShopCloseDayUpdateRequest.CloseDayDto(1, Days.MONDAY),
                                new ShopCloseDayUpdateRequest.CloseDayDto(3, Days.MONDAY)
                        ))
                        .build();

                // when
                shopService.updateCloseDays(1L, shop.getOwner(), request);

                // then
                assertThat(shop.getCloseDays().get(0).getWeekNumOfMonth()).isEqualTo(1);
                assertThat(shop.getCloseDays().get(0).getDayOfWeek()).isEqualTo(Days.MONDAY);
                assertThat(shop.getCloseDays().get(1).getWeekNumOfMonth()).isEqualTo(3);
                assertThat(shop.getCloseDays().get(1).getDayOfWeek()).isEqualTo(Days.MONDAY);
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
                .callNumber("010-1234-5678")
                .address("서울 강남구 영동대로 513")
                .categoryShop(Arrays.asList(
                        CategoryShop.builder().category(Category.builder().name("카테고리1").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리2").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리3").build()).build(),
                        CategoryShop.builder().category(Category.builder().name("카테고리4").build()).build()
                ))
                .build();

        return shop;
    }

    private Shop getShopWithOwner(Long ownerId) {
        Shop shop = getShop();
        shop.setOwner(new Owner(ownerId, "owner", "owner@yogiyo.com", "owner", ProviderType.DEFAULT));
        return shop;
    }
}