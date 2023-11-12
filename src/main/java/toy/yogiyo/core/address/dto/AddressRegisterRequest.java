package toy.yogiyo.core.address.dto;

import lombok.*;
import toy.yogiyo.core.address.domain.Address;
import toy.yogiyo.core.address.domain.AddressType;
import toy.yogiyo.core.address.domain.MemberAddress;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddressRegisterRequest {

    private Address address;
    private String nickname;
    private AddressType addressType;
    private Double longitude;
    private Double latitude;

    public MemberAddress toMemberAddress(){
        return MemberAddress.builder()
                .address(address)
                .addressType(addressType)
                .nickname(nickname)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
