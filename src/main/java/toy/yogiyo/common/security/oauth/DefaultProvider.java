package toy.yogiyo.common.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.repository.MemberRepository;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.repository.OwnerRepository;

@Component
@RequiredArgsConstructor
public class DefaultProvider implements OAuthProvider {

    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public LoginResponse getMemberInfo(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if(!encoder.matches(password, findMember.getPassword()))
            throw new AuthenticationException(ErrorCode.MEMBER_INVALID_PASSWORD);
        return LoginResponse.of(findMember);
    }

    @Override
    public LoginResponse getOwnerInfo(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        Owner findOwner = ownerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.OWNER_INVALID_PASSWORD));
        if(!encoder.matches(password, findOwner.getPassword()))
            throw new AuthenticationException(ErrorCode.OWNER_INVALID_PASSWORD);
        return LoginResponse.of(findOwner);
    }
}
