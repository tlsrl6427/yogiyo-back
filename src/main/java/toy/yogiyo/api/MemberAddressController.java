package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.address.dto.AddressRegisterRequest;
import toy.yogiyo.core.address.dto.MemberAddressResponse;
import toy.yogiyo.core.address.service.MemberAddressService;
import toy.yogiyo.core.member.domain.Member;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class MemberAddressController {

    private final MemberAddressService memberAddressService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@LoginUser Member member, @Valid @RequestBody AddressRegisterRequest request){
        memberAddressService.register(member, request);
    }

    @GetMapping("/view")
    @ResponseStatus(HttpStatus.OK)
    public MemberAddressResponse getAddresses(@LoginUser Member member){
        return memberAddressService.getAddresses(member);
    }

    @PatchMapping("/here/{memberAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setHere(@LoginUser Member member, @PathVariable Long memberAddressId){
        memberAddressService.setHere(member, memberAddressId);
    }

    @DeleteMapping("/{memberAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginUser Member member, @PathVariable Long memberAddressId){
        memberAddressService.delete(member, memberAddressId);
    }
}
