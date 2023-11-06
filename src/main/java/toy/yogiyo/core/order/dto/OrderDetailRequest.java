package toy.yogiyo.core.order.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderDetailRequest {

    private Long orderId;
}
