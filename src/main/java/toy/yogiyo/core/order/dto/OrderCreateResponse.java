package toy.yogiyo.core.order.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderCreateResponse {

    private Long orderId;

}
