package vn.phankyanh.shopbongda.extra.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.extra.domain.ExtraService;

@Repository
public interface ExtraServiceRepository
        extends JpaRepository<ExtraService, Long> {

    List<ExtraService> findByIsActiveTrue();

    boolean existsByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e FROM ExtraService e
        WHERE e.id = :id
    """)
    Optional<ExtraService> findByIdForUpdate(
            @Param("id") Long id
    );
}
