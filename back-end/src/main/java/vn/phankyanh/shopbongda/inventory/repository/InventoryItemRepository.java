package vn.phankyanh.shopbongda.inventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.phankyanh.shopbongda.inventory.domain.InventoryItem;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    boolean existsByItemCode(String itemCode);

    Optional<InventoryItem> findByItemCode(String itemCode);

    Page<InventoryItem> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<InventoryItem> findByItemType(InventoryItemType itemType, Pageable pageable);

    Page<InventoryItem> findByNameContainingIgnoreCaseAndItemType(
            String name,
            InventoryItemType itemType,
            Pageable pageable
    );
}
