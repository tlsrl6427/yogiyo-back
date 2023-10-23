package toy.yogiyo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.yogiyo.common.login.LoginUser;
import toy.yogiyo.core.Like.dto.LikeResponse;
import toy.yogiyo.core.Like.dto.LikeScrollResponse;
import toy.yogiyo.core.Like.service.LikeService;
import toy.yogiyo.core.Member.domain.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{shopId}")
    public String toggleLike(@LoginUser Member member, @PathVariable Long shopId) {
        likeService.toggleLike(member, shopId);
        return "like success";
    }

    @GetMapping("/scroll")
    public LikeScrollResponse getLikes(@LoginUser Member member, @RequestParam(defaultValue = "-1") Long lastId){
        return likeService.getLikes(member, lastId);
    }
}
