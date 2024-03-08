package toy.yogiyo.common.security.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "oauth.naver2")
@Getter
@Setter
public class OAuthNaver2Properties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private List<String> scope;
}
