package toy.yogiyo.common.security.oauth;

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
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.repository.OwnerRepository;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverProvider implements OAuthProvider{

    private final String NAVER_AUTH_URL = "https://nid.naver.com/oauth2.0/token";
    private final String NAVER_PROFILE_URL = "https://openapi.naver.com/v1/nid/me";
    private final String BEARER = "Bearer ";
    private final RestTemplate restTemplate = new RestTemplate();
    private final OAuthNaverProperties naverProperties;
    private final OAuthNaver2Properties naver2Properties;
    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;

    @Override
    public LoginResponse getMemberInfo(LoginRequest request) {
        String accessToken = getAccessToken(request.getAuthCode(), 0);
        OAuthIdTokenResponse oAuthIdTokenResponse = getUserInfo(accessToken);
        Member member = memberRepository.findByEmailAndProvider(oAuthIdTokenResponse.getEmail(), ProviderType.NAVER)
                .orElseGet(() -> autoJoin_member(oAuthIdTokenResponse));
        return LoginResponse.of(member);
    }

    @Override
    public LoginResponse getOwnerInfo(LoginRequest request) {
        String accessToken = getAccessToken(request.getAuthCode(), 1);
        OAuthIdTokenResponse oAuthIdTokenResponse = getUserInfo(accessToken);
        Owner owner = ownerRepository.findByEmailAndProvider(oAuthIdTokenResponse.getEmail(), ProviderType.NAVER)
                .orElseGet(() -> autoJoin_owner(oAuthIdTokenResponse));
        return LoginResponse.of(owner);
    }

    private Member autoJoin_member(OAuthIdTokenResponse oAuthIdTokenResponse) {
        return memberRepository.save(oAuthIdTokenResponse.toMember(ProviderType.NAVER));
    }

    private Owner autoJoin_owner(OAuthIdTokenResponse oAuthIdTokenResponse) {
        return ownerRepository.save(oAuthIdTokenResponse.toOwner(ProviderType.NAVER));
    }

    private OAuthIdTokenResponse getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER + accessToken);

        ResponseEntity<NaverUser> exchange = restTemplate.exchange(NAVER_PROFILE_URL, HttpMethod.POST, new HttpEntity<>(headers), NaverUser.class);

        NaverUser naverUser = exchange.getBody();
        NaverUser.Response response = naverUser.getResponse();
        return OAuthIdTokenResponse.builder()
                .nickname(response.getNickname())
                .email(response.getEmail())
                .build();
    }

    private String getAccessToken(String authCode, int num) {
        // body 설정
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("code", authCode);
        multiValueMap.add("client_id", num==0 ? naverProperties.getClientId() : naver2Properties.getClientId());
        multiValueMap.add("client_secret", num==0 ? naverProperties.getClientSecret() : naver2Properties.getClientSecret());
        multiValueMap.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueMap, headers);

        // 요청 보내기
        ResponseEntity<Map<String, String>> result = restTemplate.exchange(NAVER_AUTH_URL, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<>() {
        });


        return result.getBody().get("access_token");
    }
}
