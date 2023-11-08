package toy.yogiyo.core.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopNoticeUpdateRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String notice;
}
