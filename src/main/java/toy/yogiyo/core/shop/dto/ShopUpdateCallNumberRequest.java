package toy.yogiyo.core.shop.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopUpdateCallNumberRequest {

    @NotBlank
    private String callNumber;

}
