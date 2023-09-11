package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.core.Member.domain.MemberJoinRequest;
import toy.yogiyo.core.Member.domain.MemberMypageResponse;
import toy.yogiyo.core.Member.domain.MemberUpdateRequest;
import toy.yogiyo.core.Member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public Long join(MemberJoinRequest memberJoinRequest){
        return memberService.join(memberJoinRequest);
    }

    @GetMapping("/mypage/{id}")
    public MemberMypageResponse showMypage(@PathVariable Long id){
        return memberService.findOne(id);
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") Long id, MemberUpdateRequest memberUpdateRequest){
        memberService.update(id, memberUpdateRequest);
        return "update success";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id){
        memberService.delete(id);
        return "delete success";
    }
}
