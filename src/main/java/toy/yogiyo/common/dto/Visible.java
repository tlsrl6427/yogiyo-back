package toy.yogiyo.common.dto;

import lombok.AllArgsConstructor;
import toy.yogiyo.common.domain.DocsEnumType;

@AllArgsConstructor
public enum Visible implements DocsEnumType {
    SHOW("판매중"),
    SOLD_OUT("품절"),
    HIDE("숨김");

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