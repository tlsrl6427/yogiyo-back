package toy.yogiyo.api;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopScrollResponse {

    private Long shopId;
}
