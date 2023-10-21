package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.Address.dto.AddressRegisterRequest;
import toy.yogiyo.core.Address.dto.MemberAddressResponse;
import toy.yogiyo.core.Address.service.MemberAddressService;
import toy.yogiyo.core.Member.domain.Member;

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
