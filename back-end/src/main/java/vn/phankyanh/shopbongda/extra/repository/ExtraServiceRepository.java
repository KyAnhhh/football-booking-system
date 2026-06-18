package vn.phankyanh.shopbongda.extra.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.extra.domain.ExtraService;

@Repository
public interface ExtraServiceRepository
        extends JpaRepository<ExtraService, Long> {

    List<ExtraService> findByIsActiveTrue();
}
