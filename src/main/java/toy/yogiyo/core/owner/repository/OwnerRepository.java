package toy.yogiyo.core.owner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.member.domain.ProviderType;
import toy.yogiyo.core.owner.domain.Owner;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Query("select o from Owner o " +
            "where o.email=:email " +
            "and o.providerType=:providerType")
    Optional<Owner> findByEmailAndProvider(@Param("email") String email, @Param("providerType") ProviderType providerType);

    @Query("select o from Owner o " +
            "where o.email=:email " +
            "and o.password=:password " +
            "and o.providerType='DEFAULT'")
    Optional<Owner> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    Optional<Owner> findByEmail(String email);
}
