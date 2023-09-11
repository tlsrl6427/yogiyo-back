package toy.yogiyo.core.Member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.core.Order.domain.Order;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    private ProviderType providerType;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @Builder
    public Member(Long id, String nickname, String email, String password, ProviderType providerType) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.providerType = providerType;
    }

    public void update(Member member){
        if(member.getNickname() != null) this.nickname = member.getNickname();
    }
}
