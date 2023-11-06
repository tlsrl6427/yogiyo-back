package toy.yogiyo.core.owner.dto;

import lombok.*;
import toy.yogiyo.core.owner.domain.Owner;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OwnerJoinResponse {

    private Long id;

    public static OwnerJoinResponse of(Owner owner) {
        return OwnerJoinResponse.builder()
                .id(owner.getId())
                .build();
    }
}
