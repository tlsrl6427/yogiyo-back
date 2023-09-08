package toy.yogiyo.core.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.*;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.core.shop.Shop;
import toy.yogiyo.core.shop.dto.ShopRegisterRequest;
import toy.yogiyo.core.shop.dto.ShopDetailsResponse;
import toy.yogiyo.core.shop.dto.ShopUpdateRequest;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ImageFileHandler imageFileHandler;
    private final Environment env;


    // TODO : * Owner 가져오는 로직 변경해야함 *
    //  일단 임의로 Owner만들어서 사용중
    @Transactional
    public Long register(ShopRegisterRequest request) throws IOException {
        validateDuplicateName(request.getName());

        String imagePath = "/" + env.getProperty("image.path") + "/";

        String iconStoredName = imagePath + imageFileHandler.store(request.getIcon());
        String bannerStoredName = imagePath + imageFileHandler.store(request.getBanner());

        Shop shop = request.toEntity(iconStoredName, bannerStoredName);

        shop.changeOwner(new Shop.Owner());

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

        shop.changeInfo(request.getName(),
                request.getOwnerNotice(),
                request.getBusinessHours(),
                request.getCallNumber(),
                request.getAddress(),
                request.getDeliveryTime(),
                request.getLeastOrderPrice(),
                request.getOrderTypes(),
                request.getDeliveryPrice(),
                request.getPackagingPrice());
    }

    @Transactional
    public void delete(Long shopId, Long ownerId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        if (!Objects.equals(shop.getOwner().getId(), ownerId)) {
            throw new AccessDeniedException(ErrorCode.SHOP_ACCESS_DENIED);
        }

        if (!imageFileHandler.remove(getFilename(shop.getIcon()))) {
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }
        if (!imageFileHandler.remove(getFilename(shop.getBanner()))) {
            throw new FileIOException(ErrorCode.FILE_NOT_REMOVED);
        }

        shopRepository.delete(shop);
    }


    private void validateDuplicateName(String name) {
        if (shopRepository.existsByName(name)) {
            throw new EntityExistsException(ErrorCode.SHOP_ALREADY_EXIST);
        }
    }

    private String getFilename(String filenameWithPath) {
        return filenameWithPath.substring(filenameWithPath.lastIndexOf("/") + 1);
    }

}
