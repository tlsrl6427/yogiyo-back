package toy.yogiyo.core.address.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.member.domain.Member;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_member_id", columnList = "member_id"))
public class MemberAddress {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    private String nickname;

    private Double longitude;
    private Double latitude;
    private String code;

    private boolean here;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Builder
    public MemberAddress(Long id, Address address, AddressType addressType, String nickname, Double longitude, Double latitude, String code, boolean here, Member member) {
        this.id = id;
        this.address = address;
        this.addressType = addressType;
        this.nickname = nickname;
        this.longitude = longitude;
        this.latitude = latitude;
        this.code = code;
        this.here = here;
        this.member = member;
    }

    public void setMember(Member member){
        this.member = member;
    }

    public void isHere(boolean bool){
        this.here = bool;
    }
}
