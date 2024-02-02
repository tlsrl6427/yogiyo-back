package toy.yogiyo.core.menuoption.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class MenuOptionSearchRequest {
    @NotNull
    private Long shopId;
    @NotBlank
    private String keyword;
}
