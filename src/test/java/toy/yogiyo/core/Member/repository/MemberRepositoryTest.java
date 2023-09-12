package toy.yogiyo.core.Member.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    Member member;

    @BeforeEach
    void beforeEach(){
        member = Member.builder()
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .providerType(ProviderType.DEFAULT)
                .build();
    }

    @DisplayName("email, providerType으로 Member 조회")
    @Test
    void findByEmailAndProvider() {

        memberRepository.save(member);

        Member findMember = memberRepository.findByEmailAndProvider(member.getEmail(), member.getProviderType()).orElse(null);

        assertThat(findMember).isEqualTo(member);
    }
}