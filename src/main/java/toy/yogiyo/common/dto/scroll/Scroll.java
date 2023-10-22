package toy.yogiyo.common.dto.scroll;

import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class Scroll<T> {

    private List<T> content;
    private int number;
    private int size;
    boolean hasNext;

    public Scroll(List<T> content, int number, int size, boolean hasNext) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.hasNext = hasNext;
    }

    public <U> Scroll<U> map(Function<? super T, ? extends U> converter) {
        List<U> list = content.stream().map(converter).collect(Collectors.toList());
        return new Scroll<>(list, number, size, hasNext);
    }
}
