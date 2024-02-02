package toy.yogiyo.core.menu.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class MenuSearchRequest {

    @NotNull
    private Long shopId;

    @NotBlank
    private String keyword;

}
