package toy.yogiyo.core.address.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.address.domain.AddressType;
import toy.yogiyo.core.address.domain.MemberAddress;
import toy.yogiyo.core.address.dto.AddressRegisterRequest;
import toy.yogiyo.core.address.dto.MemberAddressResponse;
import toy.yogiyo.core.address.repository.MemberAddressRepository;
import toy.yogiyo.core.member.domain.Member;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberAddressService {

    private final MemberAddressRepository memberAddressRepository;

    public void register(Member member, AddressRegisterRequest request){
        if(request.getAddressType() != AddressType.ELSE){
            List<MemberAddress> memberAddresses = member.getMemberAddresses();

            memberAddresses.stream()
                    .filter(memberAddress -> memberAddress.getAddressType()==request.getAddressType())
                    .findAny()
                    .ifPresent(memberAddresses::remove);
        }
        member.addMemberAddresses(request.toMemberAddress());
    }

    @Transactional(readOnly = true)
    public MemberAddressResponse getAddresses(Member member){
        List<MemberAddress> findMemberAddress = memberAddressRepository.findByMemberId(member.getId());
        return MemberAddressResponse.from(findMemberAddress);
    }

    public void delete(Member member, Long memberAddressId){
        validateMember(member, memberAddressId);
        memberAddressRepository.deleteById(memberAddressId);
    }

    private void validateMember(Member member, Long memberAddressId) {
        MemberAddress findMemberAddress = memberAddressRepository.findById(memberAddressId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBERADDRESS_NOT_FOUND));
        if(findMemberAddress.getMember() != member) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
    }
}
