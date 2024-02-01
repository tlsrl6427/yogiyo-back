package toy.yogiyo.core.review.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
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

    public enum Sort {
        LATEST, RATING_HIGH, RATING_LOW
    }

    public enum Status {
        ALL, NO_REPLY
    }

}
