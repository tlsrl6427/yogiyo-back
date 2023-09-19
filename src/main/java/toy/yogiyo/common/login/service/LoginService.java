package toy.yogiyo.common.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.common.security.oauth.OAuthManager;
import toy.yogiyo.common.security.oauth.OAuthProvider;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;
import toy.yogiyo.core.Member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {

    private final OAuthManager oAuthManager;
    private final MemberRepository memberRepository;

    public LoginResponse login(LoginRequest loginRequest){
        ProviderType providerType = loginRequest.getProviderType();
        OAuthProvider oAuthProvider = oAuthManager.getOAuthProvider(providerType);
        return oAuthProvider.getUserInfo(loginRequest);
    }

    public Member getMember(String email, ProviderType providerType){
        Member findMember = memberRepository.findByEmailAndProvider(email, providerType)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return findMember;
    }
}
