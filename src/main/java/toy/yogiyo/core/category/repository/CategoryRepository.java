package toy.yogiyo.core.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
