package toy.yogiyo.common.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.yogiyo.core.Member.domain.ProviderType;

@Component
@RequiredArgsConstructor
public class OAuthManager {

    private final GoogleProvider googleProvider;
    private final KakaoProvider kakaoProvider;
    private final DefaultProvider defaultProvider;

    public OAuthProvider getOAuthProvider(ProviderType providerType){
        switch (providerType){
            case GOOGLE: return googleProvider;
            case KAKAO: return kakaoProvider;
            case DEFAULT: return defaultProvider;
            default: throw new IllegalArgumentException();
        }
    }

}
