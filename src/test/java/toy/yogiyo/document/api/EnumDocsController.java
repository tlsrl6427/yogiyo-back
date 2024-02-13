package toy.yogiyo.document.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.yogiyo.common.domain.DocsEnumType;
import toy.yogiyo.common.dto.Visible;
import toy.yogiyo.core.deliveryplace.dto.AdjustmentType;
import toy.yogiyo.core.menuoption.domain.OptionType;
import toy.yogiyo.core.shop.domain.Days;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class EnumDocsController {

    @GetMapping("/enum-docs")
    public ResponseEntity<EnumDocsResponse> enumDocs() {
        return ResponseEntity.ok(EnumDocsResponse.builder()
                .days(getDocs(Days.values()))
                .optionType(getDocs(OptionType.values()))
                .visible(getDocs(Visible.values()))
                .adjustmentType(getDocs(AdjustmentType.values()))
                .build()
        );
    }


    private Map<String, String> getDocs(DocsEnumType[] enumTypes) {
        return Arrays.stream(enumTypes)
                .collect(Collectors.toMap(DocsEnumType::getType, DocsEnumType::getDescription));
    }

}
