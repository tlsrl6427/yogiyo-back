package toy.yogiyo.core.Member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.domain.ProviderType;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m where m.email = :email and m.providerType = :providerType")
    Optional<Member> findByEmailAndProvider(@Param("email") String email, @Param("providerType") ProviderType providerType);

}
