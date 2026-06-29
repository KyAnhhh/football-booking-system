package vn.phankyanh.shopbongda.inventory.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReservation;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReservationStatus;

public interface InventoryReservationRepository
        extends JpaRepository<InventoryReservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from InventoryReservation r where r.id = :id")
    Optional<InventoryReservation> findByIdForUpdate(@Param("id") Long id);

    Page<InventoryReservation> findByStatus(
            InventoryReservationStatus status,
            Pageable pageable
    );

    List<InventoryReservation> findAllByStatus(
            InventoryReservationStatus status
    );
}
