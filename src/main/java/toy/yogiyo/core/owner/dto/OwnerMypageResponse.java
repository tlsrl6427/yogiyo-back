package toy.yogiyo.core.owner.dto;

import lombok.*;
import toy.yogiyo.core.owner.domain.Owner;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OwnerMypageResponse {

    private String nickname;
    private String email;

    public static OwnerMypageResponse of(Owner owner) {
        return OwnerMypageResponse.builder()
                .nickname(owner.getNickname())
                .email(owner.getEmail())
                .build();
    }
}
