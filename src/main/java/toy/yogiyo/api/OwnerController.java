package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerJoinResponse join(@RequestBody OwnerJoinRequest ownerJoinRequest){
        return ownerService.join(ownerJoinRequest);
    }

    @GetMapping("/mypage")
    @ResponseStatus(HttpStatus.OK)
    public OwnerMypageResponse getMypage(@LoginOwner Owner owner){
        return ownerService.getMypage(owner);
    }

    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@LoginOwner Owner owner, @RequestBody OwnerUpdateRequest ownerUpdateRequest){
        ownerService.update(owner, ownerUpdateRequest);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginOwner Owner owner){
        ownerService.delete(owner);
    }

}
