package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopTempCloseRequest {

    private LocalDateTime closeUntil;
    private Boolean today;

}
