package toy.yogiyo.common.security.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.login.service.LoginService;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider{

    private final JwtProperties jwtProperties;
    private final LoginService loginService;

    public String createToken(String email, ProviderType providerType){
        return Jwts.builder()
                .setSubject(email)
                .claim("providerType", providerType)
                .setExpiration(new Date(System.currentTimeMillis()+jwtProperties.getExpired()))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    public void validateToken(String token){
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException(ErrorCode.JWT_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new AuthenticationException(ErrorCode.JWT_MALFORMED);
        } catch (SignatureException e) {
            throw new AuthenticationException(ErrorCode.JWT_SIGNATURE_FAILED);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException(ErrorCode.JWT_ILLEGAL_ARGUMENT);
        }
    }

    public Authentication getAuthentication(String token){
        // 토큰 분해
        Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody();
        String email = claims.getSubject();
        ProviderType providerType = ProviderType.valueOf((String) claims.get("providerType"));

        // 이메일, 공급자에 맞는 멤버 가져오기
        Member loginMember = loginService.getMember(email, providerType);

        // 인증표시된 토큰 리턴
        return new UsernamePasswordAuthenticationToken(loginMember, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
