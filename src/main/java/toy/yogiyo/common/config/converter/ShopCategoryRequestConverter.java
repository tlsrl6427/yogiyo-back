package toy.yogiyo.common.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListRequest;

@Component
public class ShopCategoryRequestConverter implements Converter<String, ShopScrollListRequest.ShopCategory> {

    @Override
    public ShopScrollListRequest.ShopCategory convert(String source) {
            return ShopScrollListRequest.ShopCategory.parsing(source);
        }

}
