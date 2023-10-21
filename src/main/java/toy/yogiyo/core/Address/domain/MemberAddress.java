package toy.yogiyo.core.Address.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.Member.domain.Member;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddress {

    @Id @GeneratedValue
    private Long id;

    @Embedded
    private Address address;

    private AddressType addressType;
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberAddress(Address address, AddressType addressType, String nickname, Member member) {
        this.address = address;
        this.addressType = addressType;
        this.nickname = nickname;
        this.member = member;
    }
}
