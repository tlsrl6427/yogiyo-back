package toy.yogiyo.core.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.exception.*;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.category.service.CategoryShopService;
import toy.yogiyo.core.owner.service.OwnerService;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.dto.*;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ImageFileHandler imageFileHandler;
    private final CategoryShopService categoryShopService;
    private final OwnerService ownerService;

    @Transactional
    public Long register(ShopRegisterRequest request, MultipartFile icon, MultipartFile banner, Long ownerId) throws IOException {
        validateDuplicateName(request.getName());

        String iconStoredName = ImageFileUtil.getFilePath(imageFileHandler.store(icon));
        String bannerStoredName = ImageFileUtil.getFilePath(imageFileHandler.store(banner));

        Shop shop = request.toEntity(iconStoredName, bannerStoredName);

        shop.changeOwner(ownerService.findOne(ownerId));

        categoryShopService.save(request.getCategories(), shop);

        return shopRepository.save(shop).getId();
    }

    @Transactional(readOnly = true)
    public ShopDetailsResponse getDetailInfo(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        return ShopDetailsResponse.from(shop);
    }

    @Transactional
    public void updateShopInfo(Long shopId, Long ownerId, ShopUpdateRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        validatePermission(ownerId, shop);

        shop.changeInfo(request.getName(), request.getCallNumber(), request.getAddress());

        categoryShopService.changeCategory(request.getCategories(), shop);
    }

    @Transactional
    public void updateNotice(Long shopId, Long ownerId, ShopNoticeUpdateRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        validatePermission(ownerId, shop);

        shop.changeNotice(request.getNotice());
    }

    @Transactional
    public void updateBusinessHours(Long shopId, Long ownerId, ShopBusinessHourUpdateRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        validatePermission(ownerId, shop);

        shop.changeBusinessHours(request.getBusinessHours());
    }

    @Transactional
    public void updateDeliveryPrice(Long shopId, Long ownerId, DeliveryPriceUpdateRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        validatePermission(ownerId, shop);

        shop.changeDeliveryPrices(request.toEntity());
    }

    @Transactional
    public void delete(Long shopId, Long ownerId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        validatePermission(ownerId, shop);

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

    private void validatePermission(Long ownerId, Shop shop) {
        if (!Objects.equals(shop.getOwner().getId(), ownerId)) {
            throw new AccessDeniedException(ErrorCode.SHOP_ACCESS_DENIED);
        }
    }
}
