package vn.phankyanh.shopbongda.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryStock;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {
    Optional<InventoryStock> findByInventoryItemId(Long inventoryItemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from InventoryStock s where s.inventoryItem.id = :inventoryItemId")
    Optional<InventoryStock> findByInventoryItemIdForUpdate(
            @Param("inventoryItemId") Long inventoryItemId
    );
}
