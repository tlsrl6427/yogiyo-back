package toy.yogiyo.core.shop.domain;

import lombok.AllArgsConstructor;
import toy.yogiyo.common.domain.DocsEnumType;

@AllArgsConstructor
public enum Days implements DocsEnumType {
    EVERYDAY("매일"),
    SUNDAY("일요일"),
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일");

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
