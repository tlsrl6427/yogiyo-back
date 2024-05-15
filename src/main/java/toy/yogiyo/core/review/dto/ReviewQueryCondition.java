package toy.yogiyo.core.review.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import toy.yogiyo.common.domain.DocsEnumType;
import toy.yogiyo.common.dto.scroll.ScrollRequest;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewQueryCondition extends ScrollRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull
    private Sort sort;
    private Status status;

    @AllArgsConstructor
    public enum Sort implements DocsEnumType {
        LATEST("최신순"),
        RATING_HIGH("별점 높은순"),
        RATING_LOW("별점 낮은순");

        private final String description;

        @Override
        public String getType() {
            return this.name();
        }

        @Override
        public String getDescription() {
            return this.description;
        }
    }

    @AllArgsConstructor
    public enum Status implements DocsEnumType{
        ALL("전체"),
        NO_REPLY("미답변");

        private final String description;

        @Override
        public String getType() {
            return this.name();
        }

        @Override
        public String getDescription() {
            return this.description;
        }
    }

}
