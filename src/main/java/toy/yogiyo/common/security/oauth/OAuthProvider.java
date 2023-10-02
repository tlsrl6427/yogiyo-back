package toy.yogiyo.common.security.oauth;

import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;

public interface OAuthProvider {
    LoginResponse getMemberInfo(LoginRequest request);
    LoginResponse getOwnerInfo(LoginRequest request);
}
