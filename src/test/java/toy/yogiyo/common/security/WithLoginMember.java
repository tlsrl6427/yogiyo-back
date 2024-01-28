package toy.yogiyo.common.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import toy.yogiyo.core.member.domain.ProviderType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithLoginMemberSecurityContextFactory.class)
public @interface WithLoginMember {
    long id() default 1L;
    String nickname() default "member1";
    String email() default "member1@test.com";
    ProviderType providerType() default ProviderType.DEFAULT;
}
