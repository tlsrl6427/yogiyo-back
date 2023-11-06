package toy.yogiyo.core.member.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.Member;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
