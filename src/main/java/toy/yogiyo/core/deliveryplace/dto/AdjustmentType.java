package toy.yogiyo.core.deliveryplace.dto;

import lombok.AllArgsConstructor;
import toy.yogiyo.common.domain.DocsEnumType;

@AllArgsConstructor
public enum AdjustmentType implements DocsEnumType {
    INCREASE("인상"),
    DECREASE("인하");

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
