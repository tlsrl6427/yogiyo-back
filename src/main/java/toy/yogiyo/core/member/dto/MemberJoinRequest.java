package toy.yogiyo.core.member.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.domain.ProviderType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberJoinRequest {

    private String nickname;
    private String email;
    private String password;
    private ProviderType providerType;

    public Member toMember(){
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .providerType(providerType)
                .build();
    }
}
