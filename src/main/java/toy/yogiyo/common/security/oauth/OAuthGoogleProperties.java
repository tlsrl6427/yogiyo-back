package toy.yogiyo.common.security.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "oauth.google")
@Getter @Setter
public class OAuthGoogleProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private List<String> scope;
}
