package toy.yogiyo.common.security.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;
import toy.yogiyo.core.Member.repository.MemberRepository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleProvider implements OAuthProvider {

    private final String GOOGLE_AUTH_URL = "https://oauth2.googleapis.com/token";
    private final RestTemplate restTemplate = new RestTemplate();
    private final OAuthGoogleProperties googleProperties;
    private final MemberRepository memberRepository;

    @Override
    public LoginResponse getUserInfo(LoginRequest request) {
        String idToken = getIdToken(request.getAuthCode());
        OAuthIdTokenResponse oAuthIdTokenResponse = decodeIdToken(idToken);
        Member member = memberRepository.findByEmailAndProvider(oAuthIdTokenResponse.getEmail(), ProviderType.GOOGLE)
                                        .orElseGet(() -> autoJoin(oAuthIdTokenResponse));
        return LoginResponse.of(member);
    }

    private Member autoJoin(OAuthIdTokenResponse oAuthIdTokenResponse) {
        return memberRepository.save(oAuthIdTokenResponse.toMember(ProviderType.GOOGLE));
    }

    private OAuthIdTokenResponse decodeIdToken(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleProperties.getClientId())).build();
        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            return OAuthIdTokenResponse.of(googleIdToken.getPayload());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getIdToken(String authCode) {
        // body 설정
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("code", authCode);
        multiValueMap.add("client_id", googleProperties.getClientId());
        multiValueMap.add("client_secret", googleProperties.getClientSecret());
        multiValueMap.add("redirect_uri", googleProperties.getRedirectUri());
        multiValueMap.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueMap, headers);

        // 요청 보내기
        ResponseEntity<Map<String, String>> result = restTemplate.exchange(GOOGLE_AUTH_URL, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<>() {
        });


        return result.getBody().get("id_token");
    }
}
