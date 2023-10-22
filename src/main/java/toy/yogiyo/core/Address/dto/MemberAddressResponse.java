package toy.yogiyo.core.Address.dto;

import lombok.*;
import toy.yogiyo.core.Address.domain.MemberAddress;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberAddressResponse {

    private List<MemberAddress> memberAddresses;

    public static MemberAddressResponse from(List<MemberAddress> memberAddresses){
        return MemberAddressResponse.builder()
                .memberAddresses(memberAddresses)
                .build();
    }
}
