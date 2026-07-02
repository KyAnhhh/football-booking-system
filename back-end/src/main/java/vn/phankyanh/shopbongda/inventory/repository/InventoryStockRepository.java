package vn.phankyanh.shopbongda.inventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryStock;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {
    Optional<InventoryStock> findByInventoryItemId(Long inventoryItemId);

    @Query("""
            select s
            from InventoryStock s
            join s.inventoryItem i
            where (
                :search is null
                or lower(i.name) like lower(concat('%', :search, '%'))
                or lower(i.itemCode) like lower(concat('%', :search, '%'))
            )
            and (:itemType is null or i.itemType = :itemType)
            and (
                :lowStockOnly = false
                or (
                    i.lowStockThreshold > 0
                    and s.availableQuantity <= i.lowStockThreshold
                )
            )
            """)
    Page<InventoryStock> search(
            @Param("search") String search,
            @Param("itemType") InventoryItemType itemType,
            @Param("lowStockOnly") boolean lowStockOnly,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from InventoryStock s where s.inventoryItem.id = :inventoryItemId")
    Optional<InventoryStock> findByInventoryItemIdForUpdate(
            @Param("inventoryItemId") Long inventoryItemId
    );
}
