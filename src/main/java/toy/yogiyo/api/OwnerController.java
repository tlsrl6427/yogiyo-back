package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginOwner;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.dto.OwnerJoinRequest;
import toy.yogiyo.core.owner.dto.OwnerJoinResponse;
import toy.yogiyo.core.owner.dto.OwnerMypageResponse;
import toy.yogiyo.core.owner.dto.OwnerUpdateRequest;
import toy.yogiyo.core.owner.service.OwnerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {

    private final OwnerService ownerService;
    @PostMapping("/join")
    public OwnerJoinResponse join(@RequestBody OwnerJoinRequest ownerJoinRequest){
        return ownerService.join(ownerJoinRequest);
    }

    @GetMapping("/mypage")
    public OwnerMypageResponse showMypage(@LoginOwner Owner owner){
        return ownerService.showMypage(owner);
    }

    @PatchMapping("/update")
    public String update(@LoginOwner Owner owner, @RequestBody OwnerUpdateRequest ownerUpdateRequest){
        ownerService.update(owner, ownerUpdateRequest);
        return "update success";
    }

    @DeleteMapping("/delete")
    public String delete(@LoginOwner Owner owner){
        ownerService.delete(owner);
        return "delete success";
    }

}
