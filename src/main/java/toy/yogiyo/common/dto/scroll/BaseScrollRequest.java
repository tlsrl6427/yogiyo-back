package toy.yogiyo.common.dto.scroll;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseScrollRequest {

    private long offset;
    @Builder.Default
    private long limit = 10;

}
