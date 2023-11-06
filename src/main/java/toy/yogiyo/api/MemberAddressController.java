package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.address.dto.AddressRegisterRequest;
import toy.yogiyo.core.address.dto.MemberAddressResponse;
import toy.yogiyo.core.address.service.MemberAddressService;
import toy.yogiyo.core.member.domain.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class MemberAddressController {

    private final MemberAddressService memberAddressService;

    @PostMapping("/register")
    public void register(@LoginUser Member member, @RequestBody AddressRegisterRequest request){
        memberAddressService.register(member, request);
    }

    @GetMapping("/view")
    public MemberAddressResponse getAddresses(@LoginUser Member member){
        return memberAddressService.getAddresses(member);
    }

    @DeleteMapping("/{memberAddressId}")
    public void delete(@LoginUser Member member, @PathVariable Long memberAddressId){
        memberAddressService.delete(member, memberAddressId);
    }
}
