package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.UserType;
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

    @PostMapping("/memberLogin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponse> memberLogin(@RequestBody LoginRequest loginRequest){

        LoginResponse loginResponse = loginService.memberLogin(loginRequest);
        String accessToken = jwtProvider.createToken(loginRequest.getEmail(), loginRequest.getProviderType(), UserType.Member);

        //Authorization Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER + accessToken);

        return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
    }

    @PostMapping("/ownerLogin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponse> ownerLogin(@RequestBody LoginRequest loginRequest){

        LoginResponse loginResponse = loginService.ownerLogin(loginRequest);
        String accessToken = jwtProvider.createToken(loginRequest.getEmail(), loginRequest.getProviderType(), UserType.Owner);

        //Authorization Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER + accessToken);

        return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
    }
}
