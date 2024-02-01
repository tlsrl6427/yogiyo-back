package toy.yogiyo.common.dto.scroll;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Scroll<T> {

    private List<T> content;
    private Object nextCursor;
    private Object nextSubCursor;
    private boolean hasNext;

    public <U> Scroll<U> map(Function<? super T, ? extends U> converter) {
        List<U> list = content.stream().map(converter).collect(Collectors.toList());
        return new Scroll<>(list, nextCursor, nextSubCursor, hasNext);
    }
}
