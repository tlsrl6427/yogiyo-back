package toy.yogiyo.core.menuoption.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.domain.OptionType;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionGroupCreateRequest {

    private String name;
    private OptionType optionType;
    private Integer count;
    private boolean isPossibleCount;
    private List<OptionDto> options;

    @Getter
    @Builder
    public static class OptionDto {
        private String content;
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
