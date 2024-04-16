package toy.yogiyo.core.refreshToken.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.login.UserType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime expiredAt;
    private String token;
    private String parent;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private UserType userType;
}
