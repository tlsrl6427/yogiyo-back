package toy.yogiyo.core.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.category.domain.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    Optional<Category> findByName(String name);

}
