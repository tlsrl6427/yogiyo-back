package toy.yogiyo.common.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.shop.dto.scroll.ShopScrollListRequest;

@Component
public class SortOptionRequestConverter implements Converter<String, ShopScrollListRequest.SortOption> {

    @Override
    public ShopScrollListRequest.SortOption convert(String source) {
        return ShopScrollListRequest.SortOption.parsing(source);
    }

}
