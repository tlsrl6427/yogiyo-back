package toy.yogiyo.core.owner.dto;

import lombok.*;
import toy.yogiyo.core.member.domain.ProviderType;
import toy.yogiyo.core.owner.domain.Owner;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OwnerJoinRequest {

    @NotBlank
    @Size(min = 2, max = 16, message = "닉네임은 2~16자 사이입니다.")
    private String nickname;
    @NotNull
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, message = "비밀번호는 8~16자 사이입니다.")
    private String password;
    private ProviderType providerType;

    public Owner toOwner(){
        return Owner.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .providerType(providerType)
                .build();
    }
}
