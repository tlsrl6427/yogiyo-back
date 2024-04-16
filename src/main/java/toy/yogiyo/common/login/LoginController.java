package toy.yogiyo.common.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.exception.*;
import toy.yogiyo.common.login.dto.LoginRequest;
import toy.yogiyo.common.login.dto.LoginResponse;
import toy.yogiyo.common.login.service.LoginService;
import toy.yogiyo.common.security.jwt.JwtProvider;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.repository.MemberRepository;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.repository.OwnerRepository;
import toy.yogiyo.core.refreshToken.domain.RefreshToken;
import toy.yogiyo.core.refreshToken.service.RefreshTokenService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

//    private final String BEARER = "Bearer ";
    private final LoginService loginService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;

    @PostMapping("/member/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponse> memberLogin(@RequestBody LoginRequest loginRequest){

        LoginResponse loginResponse = loginService.memberLogin(loginRequest);
        String accessToken = jwtProvider.createToken(loginResponse.getEmail(), loginRequest.getProviderType(), UserType.Member);

        String newRefreshToken = jwtProvider.createRefreshToken();

        refreshTokenService.saveRefreshToken(newRefreshToken, loginResponse.getUserId(), UserType.Member);

        //Authorization Header
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createCookie(accessToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, createRefreshCookie(newRefreshToken).toString());
        return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
    }

    @PostMapping("/member/logout/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> memberLogout(@LoginUser Member member, @PathVariable Long memberId){
        if(!member.getId().equals(memberId)) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, deleteCookie().toString());
        headers.add(HttpHeaders.SET_COOKIE, deleteRefreshCookie().toString());
        return new ResponseEntity<>("멤버 로그아웃 완료", headers, HttpStatus.OK);
    }

    @PostMapping("/owner/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponse> ownerLogin(@RequestBody LoginRequest loginRequest){

        LoginResponse loginResponse = loginService.ownerLogin(loginRequest);
        String accessToken = jwtProvider.createToken(loginResponse.getEmail(), loginRequest.getProviderType(), UserType.Owner);

        String newRefreshToken = jwtProvider.createRefreshToken();

        refreshTokenService.saveRefreshToken(newRefreshToken, loginResponse.getUserId(), UserType.Owner);

        //Authorization Header
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createCookie(accessToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, createRefreshCookie(newRefreshToken).toString());
        return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
    }

    @PostMapping("/owner/logout/{ownerId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> ownerLogout(@LoginOwner Owner owner, @PathVariable Long ownerId){
        if(!owner.getId().equals(ownerId)) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, deleteCookie().toString());
        headers.add(HttpHeaders.SET_COOKIE, deleteRefreshCookie().toString());
        return new ResponseEntity<>("점주 로그아웃 완료", headers, HttpStatus.OK);
    }

    @PostMapping("/re-issue")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<String> reIssue(@CookieValue String refreshToken){

        jwtProvider.validateToken(refreshToken);

        RefreshToken findRefreshToken = null;
        try {
            findRefreshToken = refreshTokenService.reIssueRefreshToken(refreshToken);
        } catch (DuplicateReIssueException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, deleteCookie().toString());
            headers.add(HttpHeaders.SET_COOKIE, deleteRefreshCookie().toString());
            return new ResponseEntity<>("토큰 재발급 실패", headers, HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = null;
        switch (findRefreshToken.getUserType()){
            case Member: newAccessToken = getNewAccessTokenByMember(findRefreshToken.getUserId()); break;
            case Owner: newAccessToken = getNewAccessTokenByOwner(findRefreshToken.getUserId()); break;
            default: throw new AuthenticationException(ErrorCode.USERTYPE_ILLEGAL);
        }

        String newRefreshToken = jwtProvider.createRefreshToken();

        refreshTokenService.saveNewRefreshToken(newRefreshToken, findRefreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createCookie(newAccessToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, createRefreshCookie(newRefreshToken).toString());

        return new ResponseEntity<>("토큰 재발급 완료", headers, HttpStatus.OK);
    }

    private String getNewAccessTokenByOwner(Long userId) {
        Owner owner = ownerRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.OWNER_NOT_FOUND));
        return jwtProvider.createToken(owner.getEmail(), owner.getProviderType(), UserType.Owner);
    }

    private String getNewAccessTokenByMember(Long userId) {

        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return jwtProvider.createToken(member.getEmail(), member.getProviderType(), UserType.Member);
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

    private ResponseCookie deleteRefreshCookie() {
        return ResponseCookie.from("refreshToken", null)
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();
    }

    private ResponseCookie createCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
//                .maxAge(1000 * 60 * 60)//1시간
//                .domain("yogiyo-clone.shop")
                .build();
    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
//                .maxAge(1000 * 60 * 60)//1시간
//                .domain("yogiyo-clone.shop")
                .build();
    }

}
