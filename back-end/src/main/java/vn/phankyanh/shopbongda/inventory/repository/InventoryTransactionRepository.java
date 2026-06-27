package vn.phankyanh.shopbongda.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.phankyanh.shopbongda.inventory.domain.InventoryTransaction;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, Long> {

    Page<InventoryTransaction> findByInventoryItemId(
            Long inventoryItemId,
            Pageable pageable
    );
}
