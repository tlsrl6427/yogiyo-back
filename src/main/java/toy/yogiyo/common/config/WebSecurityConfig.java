package toy.yogiyo.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import toy.yogiyo.common.security.jwt.JwtExceptionHandlerFilter;
import toy.yogiyo.common.security.jwt.JwtFilter;
import toy.yogiyo.common.security.jwt.JwtProvider;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .httpBasic().disable()
                .csrf().disable()
                .cors()
                .and()
                    .authorizeRequests()
                    .antMatchers("/member/join", "/memberLogin").permitAll()
                    .antMatchers("/owner/join", "/ownerLogin").permitAll()
                    .antMatchers(HttpMethod.GET, "/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/**").authenticated()
                    .antMatchers(HttpMethod.PATCH, "/**").authenticated()
                    .antMatchers(HttpMethod.DELETE, "/**").authenticated()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new JwtExceptionHandlerFilter(), JwtFilter.class)
                .build();
    }
}
