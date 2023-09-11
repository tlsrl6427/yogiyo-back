package toy.yogiyo.core.Member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.MemberJoinRequest;
import toy.yogiyo.core.Member.domain.MemberMypageResponse;
import toy.yogiyo.core.Member.domain.MemberUpdateRequest;
import toy.yogiyo.core.Member.repository.MemberRepository;

import java.util.Optional;
import java.util.OptionalDouble;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    Member member;
    @BeforeEach
    void beforeEach(){
        member = Member.builder()
                .id(1L)
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .build();
    }

    @DisplayName("회원가입")
    @Test
    void join(){
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .build();

        given(memberRepository.findByEmailAndProvider(any(), any())).willReturn(Optional.empty());
        given(memberRepository.save(any())).willReturn(member);

        Long id = memberService.join(memberJoinRequest);

        assertAll(
                () -> verify(memberRepository).findByEmailAndProvider(any(), any()),
                () -> verify(memberRepository).save(any()),
                () -> assertThat(id).isEqualTo(member.getId())
        );
    }

    @DisplayName("이미 존재하는 회원이어서 회원가입 실패")
    @Test
    void join_fail_Entity_duple(){
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .build();

        given(memberRepository.findByEmailAndProvider(any(), any())).willReturn(Optional.ofNullable(member));

        assertThatThrownBy(() -> memberService.join(memberJoinRequest)).isInstanceOf(EntityExistsException.class);
    }

    @DisplayName("마이페이지 조회")
    @Test
    void findOne(){
        given(memberRepository.findById(any())).willReturn(Optional.ofNullable(member));

        MemberMypageResponse response = memberService.findOne(1L);

        assertAll(
            () -> assertThat(response.getNickname()).isEqualTo(member.getNickname()),
            () -> assertThat(response.getEmail()).isEqualTo(member.getEmail())
        );
    }

    @DisplayName("마이페이지 업데이트")
    @Test
    void update() {
        MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
                .nickname("test")
                .build();

        given(memberRepository.findById(any())).willReturn(Optional.ofNullable(member));

        memberService.update(1L, memberUpdateRequest);

        assertAll(
                () -> assertThat(member.getNickname()).isEqualTo(memberUpdateRequest.getNickname())
        );
    }

    @DisplayName("멤버 삭제")
    @Test
    void delete() {
        given(memberRepository.findById(any())).willReturn(Optional.ofNullable(member));

        memberService.delete(member.getId());

        verify(memberRepository).delete(member);
    }
}