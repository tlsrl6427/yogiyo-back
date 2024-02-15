package toy.yogiyo.core.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;

import java.time.LocalDateTime;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("select max(m.position) from Menu m where m.menuGroup.id = :menuGroupId")
    Integer findMaxOrder(@Param("menuGroupId") Long menuGroupId);

    @Query("select m from Menu m where m.menuGroup.id = :menuGroupId order by m.position asc")
    List<Menu> findMenus(@Param("menuGroupId") Long menuGroupId);

    @Query("select m from Menu m where m.id in :menuIds")
    List<Menu> findMenus(@Param("menuIds") Long[] menuIds);

    @Modifying
    @Query("update Menu m set m.soldOutUntil = null, m.visible = toy.yogiyo.common.dto.Visible.SHOW where m.soldOutUntil <= :datetime")
    void updateAllSoldOutFinishByDateTime(@Param("datetime") LocalDateTime dateTime);
}
