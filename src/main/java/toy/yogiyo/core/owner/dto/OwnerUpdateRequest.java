package toy.yogiyo.core.owner.dto;

import lombok.*;
import toy.yogiyo.core.owner.domain.Owner;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OwnerUpdateRequest {

    private String nickname;
    public Owner toOwner() {
        return Owner.builder()
                .nickname(nickname)
                .build();
    }
}
