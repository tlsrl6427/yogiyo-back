package toy.yogiyo.core.member.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.Member;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberJoinResponse {

    private Long id;

    public static MemberJoinResponse of(Member member) {
        return MemberJoinResponse.builder()
                .id(member.getId())
                .build();
    }
}
