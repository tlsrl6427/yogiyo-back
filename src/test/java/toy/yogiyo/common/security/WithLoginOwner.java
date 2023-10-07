package toy.yogiyo.common.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithLoginOwnerSecurityContextFactory.class)
public @interface WithLoginOwner {
    long id() default 1L;
    String nickname() default "owner";
    String email() default "owner@yogiyo.com";
}
