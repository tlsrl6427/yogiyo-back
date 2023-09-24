package toy.yogiyo.core.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.food.domain.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
