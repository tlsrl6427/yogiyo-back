package toy.yogiyo.core.Member.dto;

import lombok.*;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;

@Getter
@Builder
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
