package toy.yogiyo.common.dto.scroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public abstract class BaseScrollRequest {

    @Builder.Default
    private int size = 10;
    private int number;

}
