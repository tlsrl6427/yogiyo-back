package toy.yogiyo.common.login.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.Member.domain.ProviderType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequest {

    private String email;
    private String password;
    private String authCode;
    private ProviderType providerType;


}
