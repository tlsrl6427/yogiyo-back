package toy.yogiyo.core.member.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.Member;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberUpdateRequest {

    private String nickname;

    public Member toMember(){
        return Member.builder()
                .nickname(nickname)
                .build();
    }
}
