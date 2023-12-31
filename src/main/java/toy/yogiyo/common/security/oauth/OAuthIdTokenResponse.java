package toy.yogiyo.common.security.oauth;

import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.*;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OAuthIdTokenResponse {

    private String email;
    private String nickname;
    private ProviderType providerType;

    public static OAuthIdTokenResponse of(GoogleIdToken.Payload payload){
        return OAuthIdTokenResponse.builder()
                .email(payload.getEmail())
                .nickname((String) payload.get("name"))
                .build();
    }

    public Member toMember(ProviderType providerType){
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .providerType(providerType)
                .build();
    }
}
