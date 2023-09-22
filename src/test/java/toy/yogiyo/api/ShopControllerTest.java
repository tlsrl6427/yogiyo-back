package toy.yogiyo.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.core.category.dto.CategoryDto;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.dto.DeliveryPriceDto;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopRegisterRequest;
import toy.yogiyo.core.shop.dto.ShopUpdateRequest;
import toy.yogiyo.core.shop.service.ShopService;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopController.class)
class ShopControllerTest {

    @MockBean
    ShopService shopService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("가게 입점")
    void register() throws Exception {
        // given
        ShopRegisterRequest registerRequest = givenRegisterRequest();
        MockMultipartFile requestJson = new MockMultipartFile(
                "shopData",
                "jsonData",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(registerRequest).getBytes());
        MockMultipartFile icon = givenIcon();
        MockMultipartFile banner = givenBanner();
        when(shopService.register(any(), eq(icon), eq(banner), anyLong())).thenReturn(1L);

        // when & then
        mockMvc.perform(multipart("/shop/register")
                        .file(icon)
                        .file(banner)
                        .file(requestJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print());

        // then
        verify(shopService).register(any(), eq(icon), eq(banner), anyLong());
    }

    @Test
    @DisplayName("가게 정보 조회")
    void details() throws Exception {
        // given
        Shop shop = givenShop();
        ShopDetailsResponse response = ShopDetailsResponse.from(shop);
        when(shopService.getDetailInfo(anyLong())).thenReturn(response);

        // when & then
        mockMvc.perform(get("/shop/{shopId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shop.getId()))
                .andExpect(jsonPath("$.name").value(shop.getName()))
                .andExpect(jsonPath("$.ownerNotice").value(shop.getOwnerNotice()))
                .andExpect(jsonPath("$.businessHours").value(shop.getBusinessHours()))
                .andExpect(jsonPath("$.callNumber").value(shop.getCallNumber()))
                .andExpect(jsonPath("$.address").value(shop.getAddress()))
                .andExpect(jsonPath("$.deliveryTime").value(shop.getDeliveryTime()))
                .andExpect(jsonPath("$.orderTypes").value(shop.getOrderTypes()))
                .andExpect(jsonPath("$.packagingPrice").value(shop.getPackagingPrice()))
                .andExpect(jsonPath("$.deliveryPrices").isArray())
                .andExpect(jsonPath("$.deliveryPrices.length()").value(3))
                .andDo(print());

        // then
        verify(shopService).getDetailInfo(anyLong());
    }

    @Test
    @DisplayName("가게 정보 수정")
    void update() throws Exception {
        // given
        ShopUpdateRequest updateRequest = givenUpdateRequest();
        doNothing().when(shopService).updateInfo(anyLong(), anyLong(), any());

        // when
        mockMvc.perform(patch("/shop/{shopId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print());

        // then
        verify(shopService).updateInfo(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("가게 삭제")
    void deleteShop() throws Exception {
        // given
        doNothing().when(shopService).delete(anyLong(), anyLong());

        // when
        mockMvc.perform(delete("/shop/{shopId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print());

        // then
        verify(shopService).delete(anyLong(), anyLong());
    }

    private Shop givenShop() {
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

    private ShopUpdateRequest givenUpdateRequest() {
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