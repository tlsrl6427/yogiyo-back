package toy.yogiyo.core.address.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Address {

    private String zipcode;
    private String street;
    private String detail;
}
