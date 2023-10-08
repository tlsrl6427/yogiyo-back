package toy.yogiyo.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.common.security.WithLoginOwner;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.category.dto.CategoryDto;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.dto.*;
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

    final String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInByb3ZpZGVyVHlwZSI6IkRFRkFVTFQiLCJleHAiOjE2OTQ5NjY4Mjh9.Ls1wnxU41I99ijXRyKfkYI2w3kd-Q_qA2QgCLgpDTKk";

    @BeforeEach
    void beforeEach(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("가게 입점")
    @WithLoginOwner
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
        when(shopService.register(any(), eq(icon), eq(banner), any())).thenReturn(1L);

        // when & then
        mockMvc.perform(multipart("/shop/register")
                        .file(icon)
                        .file(banner)
                        .file(requestJson)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print());

        // then
        verify(shopService).register(any(), eq(icon), eq(banner), any());
    }


    @Test
    @DisplayName("가게 정보 조회")
    void getInfo() throws Exception {
        // given
        ShopInfoResponse response = ShopInfoResponse.from(givenShop());
        when(shopService.getInfo(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(get("/shop/{shopId}/info", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.callNumber").value(response.getCallNumber()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories.length()").value(response.getCategories().size()))
                .andDo(print());
    }

    @Test
    @DisplayName("사장님 공지 조회")
    void getNotice() throws Exception {
        // given
        ShopNoticeResponse response = ShopNoticeResponse.from(givenShop());
        when(shopService.getNotice(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(get("/shop/{shopId}/notice", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.notice").value(response.getNotice()))
                .andDo(print());
    }

    @Test
    @DisplayName("영업 시간 조회")
    void getBusinessHours() throws Exception {
        // given
        ShopBusinessHourResponse response = ShopBusinessHourResponse.from(givenShop());
        when(shopService.getBusinessHours(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(get("/shop/{shopId}/business-hours", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.businessHours").value(response.getBusinessHours()))
                .andDo(print());
    }

    @Test
    @DisplayName("배달 요금 조회")
    void getDeliveryPrice() throws Exception {
        // given
        ShopDeliveryPriceResponse response = ShopDeliveryPriceResponse.from(givenShop());
        when(shopService.getDeliveryPrice(anyLong())).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(get("/shop/{shopId}/delivery-price", 1));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryPrices").isArray())
                .andExpect(jsonPath("$.deliveryPrices.length()").value(response.getDeliveryPrices().size()))
                .andDo(print());
    }

    @Test
    @DisplayName("가게 정보 수정")
    @WithLoginOwner
    void updateInfo() throws Exception {
        // given
        ShopUpdateRequest updateRequest = new ShopUpdateRequest();
        updateRequest.setName("롯데리아 (수정됨)");
        updateRequest.setCallNumber("010-1234-5678 (수정됨)");
        updateRequest.setAddress("서울 강남구 영동대로 513 (수정됨)");
        updateRequest.setCategories(Arrays.asList(
                new CategoryDto(1L, "치킨", "picture.png"),
                new CategoryDto(2L, "피자", "picture.png"),
                new CategoryDto(3L, "분식", "picture.png")));

        doNothing().when(shopService).updateShopInfo(anyLong(), anyLong(), any());

        // when
        ResultActions result = mockMvc.perform(patch("/shop/{shopId}/info/update", 1L)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print());
    }

    @Test
    @DisplayName("사장님 공지 수정")
    @WithLoginOwner
    void updateNotice() throws Exception {
        // given
        doNothing().when(shopService).updateNotice(anyLong(), anyLong(), any());
        ShopNoticeUpdateRequest request = ShopNoticeUpdateRequest.builder()
                .notice("사장님 공지")
                .build();

        // when
        ResultActions result = mockMvc.perform(patch("/shop/{shopId}/notice/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print());
    }

    @Test
    @DisplayName("영업 시간 수정")
    @WithLoginOwner
    void updateBusinessHours() throws Exception {
        // given
        doNothing().when(shopService).updateBusinessHours(anyLong(), anyLong(), any());
        ShopBusinessHourUpdateRequest request = ShopBusinessHourUpdateRequest.builder()
                .businessHours("오전 10시 ~ 오후 10시")
                .build();

        // when
        ResultActions result = mockMvc.perform(patch("/shop/{shopId}/business-hours/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print());
    }

    @Test
    @DisplayName("배달 요금 수정")
    @WithLoginOwner
    void updateDeliveryPrice() throws Exception {
        // given
        doNothing().when(shopService).updateDeliveryPrice(anyLong(), anyLong(), any());
        DeliveryPriceUpdateRequest request = DeliveryPriceUpdateRequest.builder()
                .deliveryPrices(Arrays.asList(
                        new DeliveryPriceDto(15000, 4500),
                        new DeliveryPriceDto(25000, 3500),
                        new DeliveryPriceDto(35000, 2500)))
                .build();

        // when
        ResultActions result = mockMvc.perform(patch("/shop/{shopId}/delivery-price/update", 1)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"))
                .andDo(print());
    }

    @Test
    @DisplayName("가게 삭제")
    @WithLoginOwner
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
        registerRequest.setCategories(Arrays.asList(
                new CategoryDto(1L, "치킨", "picture.png"),
                new CategoryDto(2L, "피자", "picture.png"),
                new CategoryDto(3L, "분식", "picture.png")));

        return registerRequest;
    }

}