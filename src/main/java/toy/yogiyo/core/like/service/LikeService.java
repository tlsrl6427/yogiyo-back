package toy.yogiyo.core.like.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.dto.scroll.Scroll;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.like.dto.LikeResponse;
import toy.yogiyo.core.like.dto.LikeScrollRequest;
import toy.yogiyo.core.like.repository.LikeRepository;
import toy.yogiyo.core.like.domain.Like;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.shop.domain.Shop;
import toy.yogiyo.core.shop.repository.ShopRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final ShopRepository shopRepository;
    private final LikeRepository likeRepository;

    public void toggleLike(Member member, Long shopId){
        if(member.getId() == null) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
        Optional<Like> findLike = likeRepository.findByMemberAndShop(member.getId(), shopId);
        Shop findShop = shopRepository.findById(shopId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.SHOP_NOT_FOUND));

        if(findLike.isPresent()){
            likeRepository.delete(findLike.get());
            findShop.decreaseLikeNum();
            return;
        }

        findShop.increaseLikeNum();
        likeRepository.save(Like.toLike(member, findShop));
    }

    public List<LikeResponse> getLikes(Member member){
        if(member.getId() == null) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
        return likeRepository.findLikesByMember(member.getId());
    }
}
