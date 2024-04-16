package toy.yogiyo.core.refreshToken.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.DuplicateReIssueException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.login.UserType;
import toy.yogiyo.core.refreshToken.domain.RefreshToken;
import toy.yogiyo.core.refreshToken.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional(noRollbackFor = {DuplicateReIssueException.class})
    public RefreshToken reIssueRefreshToken(String refreshToken) {

        //1. parent에서 있는지 확인
        //1-1. parent에서 있으면 해당 로우의 member_id를 가져와서 member_id가 있는 로우들 다 삭제
        //1-2. parent에서 없으면 refreshToken을 token에서 검색하고, 있으면 새로운 로우를 만들어 parent에 refreshToken을 넣고 새로운 리프레시토큰 발급 후 token에 넣음
        // 없으면 에러 처리(유효하지 않은 리프레시 토큰)

        refreshTokenRepository.findParentByToken(refreshToken).ifPresent(this::deleteRows);

        return refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new EntityNotFoundException(ErrorCode.REFRESH_TOKEN_INVALID));
    }

    private void deleteRows(RefreshToken refreshToken) {
        refreshTokenRepository.deleteRowsByUserId(refreshToken.getUserId(), refreshToken.getUserType());
        throw new DuplicateReIssueException();
    }

    public void saveNewRefreshToken(String newRefreshToken, RefreshToken oldRefreshToken) {
        RefreshToken refreshToken = RefreshToken.builder()
                .expiredAt(LocalDateTime.now().plusWeeks(2))
                .token(newRefreshToken)
                .parent(oldRefreshToken.getToken())
                .userId(oldRefreshToken.getUserId())
                .userType(oldRefreshToken.getUserType())
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    public void saveRefreshToken(String newRefreshToken, Long userId, UserType type) {
        RefreshToken refreshToken = RefreshToken.builder()
                .expiredAt(LocalDateTime.now().plusWeeks(2))
                .token(newRefreshToken)
                .parent(null)
                .userId(userId)
                .userType(type)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void deleteExpiredRefreshToken(){
        refreshTokenRepository.deleteTokenByDateTime(LocalDateTime.now());
    }
}
