package vn.phankyanh.shopbongda.membership.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.membership.domain.MembershipTier;

@Repository
public interface MembershipTierRepository
        extends JpaRepository<MembershipTier, Long> {

    Optional<MembershipTier> findByName(String name);

    List<MembershipTier> findByIsActiveTrue();

    MembershipTier findTopByRequiredPointsLessThanEqualAndIsActiveTrueOrderByRequiredPointsDesc(
            Integer points
    );
}
