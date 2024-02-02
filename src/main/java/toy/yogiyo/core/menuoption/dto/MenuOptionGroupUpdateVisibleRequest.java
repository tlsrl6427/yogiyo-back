package toy.yogiyo.core.menuoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.common.dto.Visible;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionGroupUpdateVisibleRequest {
    @NotNull
    private Visible visible;
}
