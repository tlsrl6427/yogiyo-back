package toy.yogiyo.core.Member.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

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

    @DisplayName("멤버 업데이트")
    @Test
    void update() {
        Member updateMember = Member.builder()
                .nickname("test2")
                .build();

        member.update(updateMember);

        assertThat(member.getNickname()).isEqualTo(updateMember.getNickname());
    }
}