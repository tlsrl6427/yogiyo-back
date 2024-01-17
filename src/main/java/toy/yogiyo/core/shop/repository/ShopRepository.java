package toy.yogiyo.core.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import toy.yogiyo.core.shop.domain.Shop;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopCustomRepository {

    boolean existsByName(String name);

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Shop> findById(Long aLong);
}
