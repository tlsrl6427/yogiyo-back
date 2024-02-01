package toy.yogiyo.core.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LikeScrollRequest{

    private Long offset;
    private Long limit;

}
