package toy.yogiyo.core.Member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.dto.MemberJoinResponse;
import toy.yogiyo.core.Member.dto.MemberMypageResponse;
import toy.yogiyo.core.Member.dto.MemberUpdateRequest;
import toy.yogiyo.core.Member.repository.MemberRepository;
import toy.yogiyo.core.Member.dto.MemberJoinRequest;

import javax.persistence.EntityManager;

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
        return MemberJoinResponse.of(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberMypageResponse findOne(Member member){
        if(member.getId() == null) throw new AuthenticationException(ErrorCode.MEMBER_UNAUTHORIZATION);
        return MemberMypageResponse.of(member);
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
