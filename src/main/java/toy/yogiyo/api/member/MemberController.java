package toy.yogiyo.api.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.dto.MemberJoinRequest;
import toy.yogiyo.core.member.dto.MemberJoinResponse;
import toy.yogiyo.core.member.dto.MemberMypageResponse;
import toy.yogiyo.core.member.dto.MemberUpdateRequest;
import toy.yogiyo.core.member.service.MemberService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberJoinResponse join(@Valid  @RequestBody MemberJoinRequest memberJoinRequest){
        return memberService.join(memberJoinRequest);
    }

    @GetMapping("/mypage")
    @ResponseStatus(HttpStatus.OK)
    public MemberMypageResponse getMypage(@LoginUser Member member){
        return memberService.get(member);
    }

    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@LoginUser Member member, @RequestBody MemberUpdateRequest memberUpdateRequest){
        memberService.update(member, memberUpdateRequest);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginUser Member member){
        memberService.delete(member);
    }
}
