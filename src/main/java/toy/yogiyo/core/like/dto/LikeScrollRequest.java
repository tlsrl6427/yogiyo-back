package toy.yogiyo.core.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import toy.yogiyo.common.dto.scroll.BaseScrollRequest;

@Getter
@Builder
@AllArgsConstructor
public class LikeScrollRequest{

    private Long offset;
    private Long limit;

}
