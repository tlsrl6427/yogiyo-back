package toy.yogiyo.core.menu.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.NoRepositoryBean;
import toy.yogiyo.core.menuoption.domain.OptionType;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDetailsGetResponse {

    private Long id;
    private String name;
    private String content;
    private String picture;
    private int price;
    private long reviewNum;
    private List<OptionGroupDto> optionGroups;

    public void setOptionGroups(List<OptionGroupDto> optionGroups) {
        this.optionGroups = optionGroups;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionGroupDto {

        private Long id;
        private String name;
        private int count;
        private boolean isPossibleCount;
        private OptionType optionType;
        private List<OptionDto> options;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDto {

        private Long id;
        private String content;
        private int price;

    }
}
