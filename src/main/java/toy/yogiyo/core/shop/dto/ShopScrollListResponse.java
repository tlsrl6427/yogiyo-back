package toy.yogiyo.core.shop.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopScrollListResponse {

    private List<ShopScrollResponse> shopScrollResponses;
    private Long nextOffset;
    private boolean hasNext;
}
