package toy.yogiyo.core.review.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import toy.yogiyo.common.dto.scroll.BaseScrollRequest;

import java.time.LocalDate;

@Getter
@SuperBuilder
public class ReviewQueryCondition extends BaseScrollRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Sort sort;
    private Status status;

    // 삭제 하면 안됨
    public ReviewQueryCondition(int offset, int limit, LocalDate startDate, LocalDate endDate, Sort sort, Status status) {
        super(offset, limit);
        this.startDate = startDate;
        this.endDate = endDate;
        this.sort = sort;
        this.status = status;
    }

    public enum Sort {
        LATEST, RATING_HIGH, RATING_LOW
    }

    public enum Status {
        ALL, NO_REPLY
    }

}
