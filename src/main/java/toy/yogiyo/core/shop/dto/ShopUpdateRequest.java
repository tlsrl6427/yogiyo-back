package toy.yogiyo.core.shop.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ShopUpdateRequest {

    private String name;
    private String callNumber;
    private String address;
    private List<Long> categoryIds;

}
