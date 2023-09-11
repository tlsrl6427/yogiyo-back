package toy.yogiyo.core.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.*;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.category.service.CategoryShopService;
import toy.yogiyo.core.owner.service.OwnerService;
import toy.yogiyo.core.shop.domain.DeliveryPrice;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.dto.DeliveryPriceDto;
import toy.yogiyo.core.shop.dto.ShopRegisterRequest;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopUpdateRequest;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ImageFileHandler imageFileHandler;
    private final CategoryShopService categoryShopService;
    private final OwnerService ownerService;


    @Transactional
    public Long register(ShopRegisterRequest request, Long ownerId) throws IOException {
        validateDuplicateName(request.getName());

        String iconStoredName = ImageFileUtil.getFilePath(imageFileHandler.store(request.getIcon()));
        String bannerStoredName = ImageFileUtil.getFilePath(imageFileHandler.store(request.getBanner()));

        Shop shop = request.toEntity(iconStoredName, bannerStoredName);

        shop.changeOwner(ownerService.findOne(ownerId));

        categoryShopService.save(request.getCategoryDtos(), shop);

        return shopRepository.save(shop).getId();
    }

    @Transactional(readOnly = true)
    public ShopDetailsResponse getDetailInfo(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        return ShopDetailsResponse.from(shop);
    }

    @Transactional
    public void updateInfo(ShopUpdateRequest request, Long ownerId) {

        Shop shop = shopRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        if (!Objects.equals(shop.getOwner().getId(), ownerId)) {
            throw new AccessDeniedException(ErrorCode.SHOP_ACCESS_DENIED);
        }

        // 배달 정보 Dto -> Entity
        List<DeliveryPrice> deliveryPrices = request.getDeliveryPriceDtos().stream()
                .map(DeliveryPriceDto::toEntity)
                .collect(Collectors.toList());

        shop.changeInfo(request.getName(),
                request.getOwnerNotice(),
                request.getBusinessHours(),
                request.getCallNumber(),
                request.getAddress(),
                request.getDeliveryTime(),
                request.getOrderTypes(),
                request.getPackagingPrice(),
                deliveryPrices);

        categoryShopService.changeCategory(request.getCategoryDtos(), shop);
    }

    @Transactional
    public void delete(Long shopId, Long ownerId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        if (!Objects.equals(shop.getOwner().getId(), ownerId)) {
            throw new AccessDeniedException(ErrorCode.SHOP_ACCESS_DENIED);
        }

        if (!imageFileHandler.remove(ImageFileUtil.extractFilename(shop.getIcon()))) {
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }
        if (!imageFileHandler.remove(ImageFileUtil.extractFilename(shop.getBanner()))) {
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }

        shopRepository.delete(shop);
    }


    private void validateDuplicateName(String name) {
        if (shopRepository.existsByName(name)) {
            throw new EntityExistsException(ErrorCode.SHOP_ALREADY_EXIST);
        }
    }

}
