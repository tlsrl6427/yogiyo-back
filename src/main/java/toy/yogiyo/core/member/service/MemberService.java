package toy.yogiyo.core.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.dto.MemberJoinResponse;
import toy.yogiyo.core.member.dto.MemberMypageResponse;
import toy.yogiyo.core.member.dto.MemberUpdateRequest;
import toy.yogiyo.core.member.repository.MemberRepository;
import toy.yogiyo.core.member.dto.MemberJoinRequest;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberJoinResponse join(MemberJoinRequest memberJoinRequest){
        memberRepository.findByEmailAndProvider(memberJoinRequest.getEmail(), memberJoinRequest.getProviderType())
                .ifPresent(member -> {throw new EntityExistsException(ErrorCode.MEMBER_ALREADY_EXIST);});

        Member savedMember = memberRepository.save(memberJoinRequest.toMember());
        return MemberJoinResponse.from(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberMypageResponse get(Member member){
        if(member.getId() == null) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
        return MemberMypageResponse.from(member);
    }

    public void update(Member member, MemberUpdateRequest memberUpdateRequest){
        if(member.getId() == null) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
        member.update(memberUpdateRequest.toMember());
    }

    public void delete(Member member){
        if(member.getId() == null) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
        memberRepository.delete(member);
    }


}
