package toy.yogiyo.core.Order.dto;

import lombok.*;
import toy.yogiyo.core.Order.domain.OrderType;
import toy.yogiyo.core.Order.domain.Status;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderDetailRequest {

    private Long orderId;
}
