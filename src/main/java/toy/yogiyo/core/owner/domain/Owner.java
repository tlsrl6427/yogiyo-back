package toy.yogiyo.core.owner.domain;

import lombok.*;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.Member.domain.ProviderType;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Builder
    public Owner(Long id, String nickname, String email, String password, ProviderType providerType) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.providerType = providerType;
    }

    public void update(Owner owner) {
        if(this.nickname != owner.getNickname()) this.nickname = owner.getNickname();
    }
}
