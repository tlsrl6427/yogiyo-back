package toy.yogiyo.core.refreshToken.repository;

import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import toy.yogiyo.common.login.UserType;
import toy.yogiyo.core.refreshToken.domain.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("select rt from RefreshToken rt where rt.parent=:token")
    Optional<RefreshToken> findParentByToken(@Param("token") String token);

    @Modifying
    @Query("delete from RefreshToken rt where rt.userId=:userId and rt.userType=:userType")
    void deleteRowsByUserId(@Param("userId") Long userId, @Param("userType") UserType userType);

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("delete from RefreshToken rt where rt.expiredAt <= :datetime")
    void deleteTokenByDateTime(@Param("datetime") LocalDateTime dateTime);
}
