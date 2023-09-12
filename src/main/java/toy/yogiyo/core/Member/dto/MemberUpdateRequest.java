package toy.yogiyo.core.Member.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.Member.domain.Member;

@Getter
@Builder
public class MemberUpdateRequest {

    private String nickname;

    public Member toMember(){
        return Member.builder()
                .nickname(nickname)
                .build();
    }
}
