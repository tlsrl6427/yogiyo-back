package toy.yogiyo.core.member.repository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.yogiyo.common.config.TestQuerydslConfiguration;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.domain.ProviderType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestQuerydslConfiguration.class)
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

   /* @DisplayName("email, providerType으로 Member 조회")
    @Test
    void findByEmailAndProvider() {

        memberRepository.save(member);

        Member findMember = memberRepository.findByEmailAndProvider(member.getEmail(), member.getProviderType()).orElse(null);

        assertThat(findMember).isEqualTo(member);
    }*/

}
