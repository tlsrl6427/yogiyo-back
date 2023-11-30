package toy.yogiyo.common.dto.scroll;

import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class Scroll<T> {

    private List<T> content;
    private long nextOffset;
    private boolean hasNext;

    public Scroll(List<T> content, long nextOffset, boolean hasNext) {
        this.content = content;
        this.nextOffset = nextOffset;
        this.hasNext = hasNext;
    }

    public <U> Scroll<U> map(Function<? super T, ? extends U> converter) {
        List<U> list = content.stream().map(converter).collect(Collectors.toList());
        return new Scroll<>(list, nextOffset, hasNext);
    }
}
