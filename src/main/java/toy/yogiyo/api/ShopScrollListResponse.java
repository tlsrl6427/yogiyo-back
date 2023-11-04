package toy.yogiyo.api;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopScrollListResponse {

    private List<ShopScrollResponse> shopScrollResponses;
    private Long lastId;
    private boolean hasNext;
}
