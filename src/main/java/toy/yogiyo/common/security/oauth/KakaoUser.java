package toy.yogiyo.common.security.oauth;

import lombok.Data;

@Data
public class KakaoUser {

    private String aud;
    private String sub;
    private String auth_time;
    private String iss;
    private String nickname;
    private String exp;
    private String iat;
    private String email;
}
