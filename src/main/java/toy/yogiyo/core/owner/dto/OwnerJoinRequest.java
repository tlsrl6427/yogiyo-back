package toy.yogiyo.core.owner.dto;

import lombok.*;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;
import toy.yogiyo.core.owner.domain.Owner;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OwnerJoinRequest {

    private String nickname;
    private String email;
    private String password;
    private ProviderType providerType;

    public Owner toOwner(){
        return Owner.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .providerType(providerType)
                .build();
    }
}
