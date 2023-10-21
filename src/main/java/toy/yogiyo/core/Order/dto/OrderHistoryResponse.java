package toy.yogiyo.core.Order.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderHistoryResponse {

    private List<OrderHistory> orderHistories;
    private Long lastId;
    private boolean hasNext;
}
