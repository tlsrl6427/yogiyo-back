package toy.yogiyo.core.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

}
