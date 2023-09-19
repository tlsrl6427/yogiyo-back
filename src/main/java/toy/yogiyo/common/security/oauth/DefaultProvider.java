package toy.yogiyo.common.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class DefaultProvider implements OAuthProvider {

    private final MemberRepository memberRepository;

    @Override
    public LoginResponse getUserInfo(LoginRequest request){
        String email = request.getEmail();
        String password = request.getPassword();
        Member findMember = memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return LoginResponse.of(findMember);
    }
}
