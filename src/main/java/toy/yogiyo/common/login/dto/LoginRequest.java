package toy.yogiyo.common.login.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.ProviderType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequest {

    private String email;
    private String password;
    private String authCode;
    private ProviderType providerType;


}
