package toy.yogiyo.common.login.dto;

import lombok.Builder;
import lombok.Getter;
import toy.yogiyo.core.Member.domain.Member;

@Getter
@Builder
public class LoginResponse {

    private Long userId;

    public static LoginResponse of(Member member){
        return LoginResponse.builder()
                .userId(member.getId())
                .build();
    }
}
