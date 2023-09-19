package toy.yogiyo.core.owner.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Owner {

    @Id
    @GeneratedValue
    private Long id;

    public Owner() {
    }

    public Owner(Long id) {
        this.id = id;
    }
}
