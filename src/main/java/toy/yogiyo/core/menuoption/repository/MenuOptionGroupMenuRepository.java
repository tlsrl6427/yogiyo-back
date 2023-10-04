package toy.yogiyo.core.menuoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroupMenu;

public interface MenuOptionGroupMenuRepository extends JpaRepository<MenuOptionGroupMenu, Long> {

    @Modifying
    @Query("delete from MenuOptionGroupMenu mogm where mogm.menuOptionGroup.id = :menuOptionGroupId")
    int deleteByMenuOptionGroupId(@Param("menuOptionGroupId") Long menuOptionGroupId);

}
