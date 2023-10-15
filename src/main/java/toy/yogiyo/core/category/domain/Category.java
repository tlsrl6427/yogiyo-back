package toy.yogiyo.core.category.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
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

    public void changeName(String name) {
        this.name = name;
    }

    public void changePicture(String picture) {
        this.picture = picture;
    }
}
