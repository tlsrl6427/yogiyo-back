package toy.yogiyo.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.domain.ProviderType;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m " +
            "where m.email=:email " +
            "and m.providerType=:providerType")
    Optional<Member> findByEmailAndProvider(@Param("email") String email, @Param("providerType") ProviderType providerType);

    @Query("select m from Member m " +
            "where m.email=:email " +
            "and m.password=:password " +
            "and m.providerType='DEFAULT'")
    Optional<Member> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}
