package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.like.dto.LikeScrollResponse;
import toy.yogiyo.core.like.service.LikeService;
import toy.yogiyo.core.member.domain.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{shopId}")
    @ResponseStatus(HttpStatus.OK)
    public void toggleLike(@LoginUser Member member, @PathVariable Long shopId) {
        likeService.toggleLike(member, shopId);
    }

    @GetMapping("/scroll")
    @ResponseStatus(HttpStatus.OK)
    public LikeScrollResponse getLikes(@LoginUser Member member, @RequestParam(defaultValue = "-1") Long lastId){
        return likeService.getLikes(member, lastId);
    }
}
