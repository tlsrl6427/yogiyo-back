package toy.yogiyo.common.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;

@Component
@RequiredArgsConstructor

public class KakaoProvider implements OAuthProvider {
    @Override
    public LoginResponse getUserInfo(LoginRequest request) {
        return null;
    }
}
