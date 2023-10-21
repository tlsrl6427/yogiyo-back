package toy.yogiyo.common.converter;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class StringArrayConverterTest {

    StringArrayConverter stringArrayConverter = new StringArrayConverter();

    @Test
    void convertToDatabaseColumn() {
        String result = stringArrayConverter.convertToDatabaseColumn(List.of("AA,BB,CC,DD"));
        assertThat(result).isEqualTo("AA,BB,CC,DD");
    }

    @Test
    void convertToEntityAttribute() {
        List<String> result = stringArrayConverter.convertToEntityAttribute("AA,BB,CC,DD");
        assertThat(result).contains("AA", "BB", "CC", "DD");
    }

}