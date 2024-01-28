package toy.yogiyo.common.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import toy.yogiyo.core.member.domain.Member;

import java.util.List;

public class WithLoginMemberSecurityContextFactory implements WithSecurityContextFactory<WithLoginMember> {

    @Override
    public SecurityContext createSecurityContext(WithLoginMember annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member member = Member.builder()
                .id(annotation.id())
                .nickname(annotation.nickname())
                .email(annotation.email())
                .providerType(annotation.providerType())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        context.setAuthentication(authentication);
        return context;
    }
}
