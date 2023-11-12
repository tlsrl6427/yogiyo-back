package toy.yogiyo.common.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.exception.IllegalArgumentException;
import toy.yogiyo.core.member.domain.ProviderType;

@Component
@RequiredArgsConstructor
public class OAuthManager {

    private final GoogleProvider googleProvider;
    private final KakaoProvider kakaoProvider;
    private final NaverProvider naverProvider;
    private final DefaultProvider defaultProvider;

    public OAuthProvider getOAuthProvider(ProviderType providerType){
        switch (providerType){
            case GOOGLE: return googleProvider;
            case KAKAO: return kakaoProvider;
            case NAVER: return naverProvider;
            case DEFAULT: return defaultProvider;
            default: throw new IllegalArgumentException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
        }
    }

}
