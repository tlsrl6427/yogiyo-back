package toy.yogiyo.core.menuoption.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.domain.OptionType;
import toy.yogiyo.core.shop.domain.Shop;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionGroupCreateRequest {

    @NotBlank
    private String name;
    @NotNull
    private OptionType optionType;
    @NotNull
    @Min(0)
    private Integer count;
    private boolean isPossibleCount;
    @NotNull @Valid
    private List<OptionDto> options;

    @Getter
    @Builder
    public static class OptionDto {
        @NotBlank
        private String content;
        @Min(0)
        private int price;
    }

    public MenuOptionGroup toMenuOptionGroup(Long shopId) {
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .name(name)
                .optionType(optionType)
                .count(count)
                .isPossibleCount(isPossibleCount)
                .shop(Shop.builder().id(shopId).build())
                .build();

        options.forEach(option ->
                menuOptionGroup.createMenuOption(option.getContent(), option.getPrice()));

        return menuOptionGroup;
    }

    @JsonProperty("isPossibleCount")
    public boolean isPossibleCount() {
        return isPossibleCount;
    }
}
