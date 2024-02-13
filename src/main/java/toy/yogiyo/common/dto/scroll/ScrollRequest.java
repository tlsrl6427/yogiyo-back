package toy.yogiyo.common.dto.scroll;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ScrollRequest {

    private Object cursor;
    private Object subCursor;
    @Builder.Default
    private long limit = 10;

}
