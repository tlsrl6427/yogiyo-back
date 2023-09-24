package toy.yogiyo.core.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.menu.domain.MenuGroup;


public interface MenuGroupRepository extends JpaRepository<MenuGroup, Long> {

}
