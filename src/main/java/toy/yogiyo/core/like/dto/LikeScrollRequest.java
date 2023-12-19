package toy.yogiyo.core.like.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import toy.yogiyo.common.dto.scroll.BaseScrollRequest;

@Getter
@SuperBuilder
public class LikeScrollRequest extends BaseScrollRequest {

    private Long lastId;

    public LikeScrollRequest(long offset, long limit) {
        super(offset, limit);
    }
}
