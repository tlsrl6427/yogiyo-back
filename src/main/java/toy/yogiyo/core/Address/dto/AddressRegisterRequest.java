package toy.yogiyo.core.Address.dto;

import lombok.*;
import toy.yogiyo.core.Address.domain.Address;
import toy.yogiyo.core.Address.domain.AddressType;
import toy.yogiyo.core.Address.domain.MemberAddress;
import toy.yogiyo.core.Member.domain.Member;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddressRegisterRequest {

    private Address address;
    private String nickname;
    private AddressType addressType;

    public MemberAddress toMemberAddress(){
        return MemberAddress.builder()
                .address(address)
                .addressType(addressType)
                .nickname(nickname)
                .build();
    }
}
