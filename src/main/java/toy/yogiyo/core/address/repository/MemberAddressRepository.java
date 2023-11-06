package toy.yogiyo.core.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.yogiyo.core.address.domain.MemberAddress;

import java.util.List;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {

    @Query("select ma from MemberAddress ma where ma.member.id = :memberId")
    List<MemberAddress> findByMemberId(@Param("memberId") Long memberId);
}
