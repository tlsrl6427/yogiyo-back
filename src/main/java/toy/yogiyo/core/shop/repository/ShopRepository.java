package toy.yogiyo.core.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.shop.domain.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopCustomRepository {

    boolean existsByName(String name);
}
