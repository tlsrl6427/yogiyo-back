package toy.yogiyo.common.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.login.UserType;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.common.security.oauth.OAuthManager;
import toy.yogiyo.common.security.oauth.OAuthProvider;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;
import toy.yogiyo.core.Member.repository.MemberRepository;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.repository.OwnerRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {

    private final OAuthManager oAuthManager;
    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;

    public LoginResponse memberLogin(LoginRequest loginRequest){
        ProviderType providerType = loginRequest.getProviderType();
        OAuthProvider oAuthProvider = oAuthManager.getOAuthProvider(providerType);
        return oAuthProvider.getMemberInfo(loginRequest);
    }

    public LoginResponse ownerLogin(LoginRequest loginRequest){
        ProviderType providerType = loginRequest.getProviderType();
        OAuthProvider oAuthProvider = oAuthManager.getOAuthProvider(providerType);
        return oAuthProvider.getOwnerInfo(loginRequest);
    }

    public Member getMember(String email, ProviderType providerType){
        Member findMember = memberRepository.findByEmailAndProvider(email, providerType)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return findMember;
    }

    public Owner getOwner(String email, ProviderType providerType){
        Owner findOwner = ownerRepository.findByEmailAndProvider(email, providerType)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.OWNER_NOT_FOUND));
        return findOwner;
    }

}
