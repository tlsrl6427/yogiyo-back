package toy.yogiyo.core.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.menu.domain.MenuGroupItem;

import java.util.List;
import java.util.Optional;

public interface MenuGroupItemRepository extends JpaRepository<MenuGroupItem, Long> {
    @Query("select m from MenuGroupItem m join fetch m.menu where m.menuGroup.id = :menuGroupId order by m.position asc")
    List<MenuGroupItem> findMenus(@Param("menuGroupId") Long menuGroupId);

    Optional<MenuGroupItem> findByMenuId(Long menuId);

    @Query("select max(m.position) from MenuGroupItem m where m.menuGroup.id = :menuGroupId")
    Integer findMaxOrder(@Param("menuGroupId") Long menuGroupId);
}
