package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.common.login.service.LoginService;
import toy.yogiyo.common.security.jwt.JwtProvider;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final String BEARER = "Bearer ";
    private final LoginService loginService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){

        LoginResponse loginResponse = loginService.login(loginRequest);
        String accessToken = jwtProvider.createToken(loginRequest.getEmail(), loginRequest.getProviderType());

        //Authorization Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER + accessToken);

        return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
    }
}
