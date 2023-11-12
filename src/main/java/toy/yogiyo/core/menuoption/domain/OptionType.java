package toy.yogiyo.core.menuoption.domain;

import lombok.AllArgsConstructor;
import toy.yogiyo.common.domain.DocsEnumType;

@AllArgsConstructor
public enum OptionType implements DocsEnumType {
    REQUIRED("필수"),
    OPTIONAL("선택");

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
