package toy.yogiyo.common.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.yogiyo.common.exception.*;
import toy.yogiyo.common.exception.IllegalArgumentException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            setAuthErrorResponse(response, e);
        } catch (IllegalArgumentException e){
            setIllegalArguResponse(response, e);
        } catch (ExpiredJwtException e){
            setExpiredJwtResponse(response, e);
        }
    }

    private void setExpiredJwtResponse(HttpServletResponse response, ExpiredJwtException e) throws IOException {
        ObjectMapper om = new ObjectMapper();
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie().toString());
        response.getWriter().write(om.writeValueAsString(errorResponse));
    }

    private ResponseCookie deleteCookie() {
        return ResponseCookie.from("accessToken", null)
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();
    }

    private void setIllegalArguResponse(HttpServletResponse response, IllegalArgumentException e) throws IOException {
        ObjectMapper om = new ObjectMapper();
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(om.writeValueAsString(errorResponse));
    }

    private void setAuthErrorResponse(HttpServletResponse response, AuthenticationException e) throws IOException {
        ObjectMapper om = new ObjectMapper();
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(om.writeValueAsString(errorResponse));

    }
}
