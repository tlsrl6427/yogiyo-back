package toy.yogiyo.core.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopCustomRepository {

    boolean existsByName(String name);

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Shop> findById(Long aLong);

    @Query("select s from Shop s" +
            " join MenuGroup mg on mg.shop.id = s.id" +
            " join Menu m on m.menuGroup.id = mg.id" +
            " where m.id = :menuId")
    Optional<Shop> findByMenu(@Param("menuId") Long menuId);

    @Modifying
    @Query("update Shop s set s.closeUntil = null where s.closeUntil <= :datetime")
    void updateAllTempCloseFinishByDateTime(@Param("datetime") LocalDateTime dateTime);

    List<Shop> findByOwner(Owner owner);

    @Query("select s.name from Shop s" +
            " join DeliveryPlace dp on dp.shop.id=s.id" +
            " where dp.code=:code" +
            " and s.name like %:shopName%")
    List<String> getSearchShopNames(@Param("code") String code, @Param("shopName") String shopName);
}
