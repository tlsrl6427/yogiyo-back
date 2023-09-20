package toy.yogiyo.core.Member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.yogiyo.common.domain.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;
    private String nickname;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Builder
    public Member(Long id, String nickname, String email, String password, ProviderType providerType) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.providerType = providerType;
    }

    public void update(Member member){
        if(this.nickname != member.getNickname()) this.nickname = member.getNickname();
    }
}
