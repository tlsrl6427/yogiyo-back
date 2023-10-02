package toy.yogiyo.core.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.menu.domain.SignatureMenu;

import java.util.List;
import java.util.Optional;

public interface SignatureMenuRepository extends JpaRepository<SignatureMenu, Long> {

    @Query("select max(sm.position) from SignatureMenu sm where sm.shop.id = :shopId")
    Integer findMaxOrder(@Param("shopId") Long shopId);

    @Query("select sm from SignatureMenu sm join fetch sm.menu where sm.shop.id = :shopId order by sm.position asc")
    List<SignatureMenu> findAlLByShopId(@Param("shopId") Long shopId);

    @Modifying
    @Query("delete from SignatureMenu sm where sm.shop.id = :shopId")
    int deleteAllByShopId(@Param("shopId") Long shopId);

    Optional<SignatureMenu> findByMenuId(Long menuId);
}
