package toy.yogiyo.core.Member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.dto.MemberMypageResponse;
import toy.yogiyo.core.Member.dto.MemberUpdateRequest;
import toy.yogiyo.core.Member.repository.MemberRepository;
import toy.yogiyo.core.Member.dto.MemberJoinRequest;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Long join(MemberJoinRequest memberJoinRequest){
        memberRepository.findByEmailAndProvider(memberJoinRequest.getEmail(), memberJoinRequest.getProviderType())
                .ifPresent(member -> {throw new EntityExistsException(ErrorCode.MEMBER_ALREADY_EXIST);});

        return memberRepository.save(memberJoinRequest.toMember()).getId();
    }

    public MemberMypageResponse findOne(Long id){
        Member findMember = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberMypageResponse.of(findMember);
    }

    public void update(Long id, MemberUpdateRequest memberUpdateRequest){
        Member findMember = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        findMember.update(memberUpdateRequest.toMember());
    }

    public void delete(Long id){
        Member findMember = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.delete(findMember);
    }


}
