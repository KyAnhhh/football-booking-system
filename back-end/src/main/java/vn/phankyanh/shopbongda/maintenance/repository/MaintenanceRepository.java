package vn.phankyanh.shopbongda.maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.maintenance.domain.Maintenance;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByFieldId(Long fieldId);

    List<Maintenance> findByFieldIdAndStartTimeAfter(Long fieldId, LocalDateTime startTime);

    @Query("""
        SELECT m FROM Maintenance m
        WHERE m.fieldId = :fieldId
        AND m.startTime < :endTime
        AND m.endTime > :startTime
    """)
    List<Maintenance> findConflictingMaintenances(
            @Param("fieldId") Long fieldId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
        SELECT m FROM Maintenance m
        WHERE m.fieldId = :fieldId
        AND m.startTime <= :checkTime
        AND m.endTime > :checkTime
    """)
    List<Maintenance> findActiveMaintenanceAt(
            @Param("fieldId") Long fieldId,
            @Param("checkTime") LocalDateTime checkTime
    );

    @Query("""
        SELECT m FROM Maintenance m
        WHERE m.startTime <= :checkTime
        AND m.endTime > :checkTime
        ORDER BY m.endTime ASC
    """)
    List<Maintenance> findActiveMaintenancesAt(
            @Param("checkTime") LocalDateTime checkTime
    );

    boolean existsByFieldIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Long fieldId, LocalDateTime startTime, LocalDateTime endTime
    );
}
