package toy.yogiyo.core.menuoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;

import java.util.List;

public interface MenuOptionGroupRepository extends JpaRepository<MenuOptionGroup, Long> {

    @Query("select max(mog.position) from MenuOptionGroup mog where mog.shop.id = :shopId")
    Integer findMaxOrder(@Param("shopId") Long shopId);

    @Query("select mog from MenuOptionGroup mog where mog.shop.id = :shopId order by mog.position asc")
    List<MenuOptionGroup> findAllByShopId(@Param("shopId") Long shopId);

    @Query("select distinct mog from MenuOptionGroup mog" +
            " join fetch mog.menuOptions mo" +
            " where mog.shop.id = :shopId" +
            " order by mog.position, mo.position")
    List<MenuOptionGroup> findAllWithOptionByShopId(@Param("shopId") Long shopId);
}
