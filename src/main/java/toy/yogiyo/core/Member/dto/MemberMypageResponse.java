package toy.yogiyo.core.Member.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.Member.domain.Member;

@Getter
@Builder
public class MemberMypageResponse {
    private String nickname;
    private String email;

    public static MemberMypageResponse of(Member member){
        return MemberMypageResponse.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
