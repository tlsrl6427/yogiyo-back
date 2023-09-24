package toy.yogiyo.core.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.menu.domain.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
