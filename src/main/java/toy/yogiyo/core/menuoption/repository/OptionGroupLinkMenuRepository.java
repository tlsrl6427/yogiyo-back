package toy.yogiyo.core.menuoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.menuoption.domain.OptionGroupLinkMenu;

public interface OptionGroupLinkMenuRepository extends JpaRepository<OptionGroupLinkMenu, Long> {

    @Modifying
    @Query("delete from OptionGroupLinkMenu lm where lm.menuOptionGroup.id = :menuOptionGroupId")
    int deleteByMenuOptionGroupId(@Param("menuOptionGroupId") Long menuOptionGroupId);

}
