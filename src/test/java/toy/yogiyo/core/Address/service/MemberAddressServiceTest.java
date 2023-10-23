package toy.yogiyo.core.Address.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.core.Address.domain.Address;
import toy.yogiyo.core.Address.domain.AddressType;
import toy.yogiyo.core.Address.domain.MemberAddress;
import toy.yogiyo.core.Address.dto.AddressRegisterRequest;
import toy.yogiyo.core.Address.dto.MemberAddressResponse;
import toy.yogiyo.core.Address.repository.MemberAddressRepository;
import toy.yogiyo.core.Member.domain.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberAddressServiceTest {

    @InjectMocks
    MemberAddressService memberAddressService;

    @Mock
    MemberAddressRepository memberAddressRepository;

    Member member;

    @BeforeEach
    void beforeEach(){
        member = Member.builder()
                .id(1L)
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .memberAddresses(
                        new ArrayList<>(
                                Arrays.asList(
                                        MemberAddress.builder()
                                        .id(1L)
                                        .nickname("우리집")
                                        .addressType(AddressType.HOME)
                                        .address(new Address("02833", "공릉로 232", "대성빌라 504호"))
                                        .latitude(34.2323494)
                                        .longitude(43.5549921)
                                        .build()
                                )
                        )
                )
                .build();
    }

    @DisplayName("주소 등록 - Home, Company 일경우 덮어쓰기")
    @Test
    void register() {
        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .nickname("내 집")
                .addressType(AddressType.HOME)
                .address(new Address("11111", "공릉로 111", "대성빌라 111호"))
                .latitude(11.21111111)
                .longitude(22.222222)
                .build();

        memberAddressService.register(member, request);

        assertThat(member.getMemberAddresses().get(0).getNickname()).isEqualTo("내 집");
    }

    @DisplayName("주소 등록 - Else 일경우 추가")
    @Test
    void register_else() {
        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .nickname("친구집")
                .addressType(AddressType.ELSE)
                .address(new Address("02833", "공릉로 232", "대성빌라 504호"))
                .latitude(34.2323494)
                .longitude(43.5549921)
                .build();

        memberAddressService.register(member, request);

        assertThat(member.getMemberAddresses().size()).isEqualTo(2);
    }

    @DisplayName("주소 목록 조회")
    @Test
    void getAddresses() {
        List<MemberAddress> memberAddresses = List.of(
                MemberAddress.builder()
                        .id(1L)
                        .nickname("우리집")
                        .addressType(AddressType.HOME)
                        .address(new Address("02833", "공릉로 232", "대성빌라 504호"))
                        .latitude(34.2323494)
                        .longitude(43.5549921)
                        .build(),
                MemberAddress.builder()
                        .id(2L)
                        .nickname("친구집")
                        .addressType(AddressType.ELSE)
                        .address(new Address("02833", "공릉로 232", "대성빌라 504호"))
                        .latitude(34.2323494)
                        .longitude(43.5549921)
                        .build()
        );

        given(memberAddressRepository.findByMemberId(any())).willReturn(memberAddresses);

        MemberAddressResponse addresses = memberAddressService.getAddresses(member);

        assertThat(addresses.getMemberAddresses().size()).isEqualTo(2);
    }

    @DisplayName("주소 삭제")
    @Test
    void delete() {
        MemberAddress memberAddress = MemberAddress.builder()
                .id(1L)
                .nickname("우리집")
                .addressType(AddressType.HOME)
                .address(new Address("02833", "공릉로 232", "대성빌라 504호"))
                .latitude(34.2323494)
                .longitude(43.5549921)
                .member(member)
                .build();
        given(memberAddressRepository.findById(anyLong())).willReturn(Optional.ofNullable(memberAddress));

        memberAddressService.delete(member,1L);

        verify(memberAddressRepository).findById(anyLong());
        verify(memberAddressRepository).deleteById(anyLong());
    }
}