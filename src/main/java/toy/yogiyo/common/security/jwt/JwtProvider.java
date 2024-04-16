package toy.yogiyo.common.security.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.exception.IllegalArgumentException;
import toy.yogiyo.common.login.UserType;
import toy.yogiyo.common.login.service.LoginService;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.domain.ProviderType;
import toy.yogiyo.core.owner.domain.Owner;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider{

    private final JwtProperties jwtProperties;
    private final LoginService loginService;

    public String createToken(String email, ProviderType providerType, UserType userType) {
        return Jwts.builder()
                .setSubject(email)
                .claim("providerType", providerType)
                .claim("userType", userType)
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpired()))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    public String createRefreshToken() {
        return Jwts.builder()
                .setSubject("refreshToken")
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpired()))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    public void validateToken(String token){
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new toy.yogiyo.common.exception.ExpiredJwtException(ErrorCode.JWT_EXPIRED);
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
        UserType userType = UserType.valueOf((String) claims.get("userType"));

        // 유저타입에 따라 토큰 가져오기
        switch (userType){
            case Member: return getMemberAuthToken(email, providerType);
            case Owner: return getOwnerAuthToken(email, providerType);
            default: throw new IllegalArgumentException(ErrorCode.USERTYPE_ILLEGAL);
        }

    }

    private Authentication getOwnerAuthToken(String email, ProviderType providerType) {
        Owner loginOwner = loginService.getOwner(email, providerType);
        return new UsernamePasswordAuthenticationToken(loginOwner, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Authentication getMemberAuthToken(String email, ProviderType providerType) {
        Member loginMember = loginService.getMember(email, providerType);
        return new UsernamePasswordAuthenticationToken(loginMember, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
