package toy.yogiyo.core.category.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;
    private String picture;

    public Category(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }

    public Category(Long id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }
}
