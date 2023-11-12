package toy.yogiyo.document.api;


import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class EnumDocsResponse {

    Map<String, String> days;
    Map<String, String> optionType;
}
