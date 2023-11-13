package toy.yogiyo.common.security.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.domain.ProviderType;
import toy.yogiyo.core.member.repository.MemberRepository;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoProvider implements OAuthProvider {

    private final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/token";
    private final RestTemplate restTemplate = new RestTemplate();
    private final OAuthKakaoProperties kakaoProperties;
    private final MemberRepository memberRepository;

    @Override
    public LoginResponse getMemberInfo(LoginRequest request) {
        String idToken = getIdToken(request.getAuthCode());
        OAuthIdTokenResponse oAuthIdTokenResponse = decodeIdToken(idToken);
        Member member = memberRepository.findByEmailAndProvider(oAuthIdTokenResponse.getEmail(), ProviderType.KAKAO)
                .orElseGet(() -> autoJoin(oAuthIdTokenResponse));
        return LoginResponse.of(member);
    }

    private Member autoJoin(OAuthIdTokenResponse oAuthIdTokenResponse) {
        return memberRepository.save(oAuthIdTokenResponse.toMember(ProviderType.KAKAO));
    }

    private OAuthIdTokenResponse decodeIdToken(String idToken){
        String encodedPayload = idToken.split("\\.")[1];
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(encodedPayload));
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUser kakaoUser;
        try {
            kakaoUser = objectMapper.readValue(payload, KakaoUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return OAuthIdTokenResponse.from(kakaoUser);
    }

    private String getIdToken(String authCode) {
        // body 설정
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("code", authCode);
        multiValueMap.add("client_id", kakaoProperties.getClientId());
        multiValueMap.add("client_secret", kakaoProperties.getClientSecret());
        multiValueMap.add("redirect_uri", kakaoProperties.getRedirectUri());
        multiValueMap.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueMap, headers);

        // 요청 보내기
        ResponseEntity<Map<String, String>> result = restTemplate.exchange(KAKAO_AUTH_URL, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<>() {
        });


        return result.getBody().get("id_token");
    }

    @Override
    public LoginResponse getOwnerInfo(LoginRequest request) {
        return null;
    }
}
