package toy.yogiyo.core.shop.dto.scroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ShopScrollListResponse {
    private List<ShopScrollResponse> content;
    private BigDecimal nextCursor;
    private Long nextSubCursor;
    private boolean hasNext;
}
