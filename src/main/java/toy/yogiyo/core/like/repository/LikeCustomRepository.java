package toy.yogiyo.core.like.repository;

import toy.yogiyo.core.like.dto.LikeResponse;

import java.util.List;

public interface LikeCustomRepository {
    List<LikeResponse> findLikesByMember(Long memberId);
}
