package vn.phankyanh.shopbongda.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import vn.phankyanh.shopbongda.inventory.domain.InventoryItem;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    boolean existsByItemCode(String itemCode);

    Optional<InventoryItem> findByItemCode(String itemCode);

    Optional<InventoryItem> findByItemTypeAndReferenceId(
            InventoryItemType itemType,
            Long referenceId
    );

    @Query("""
            select i
            from InventoryItem i
            where (
                :search is null
                or lower(i.name) like lower(concat('%', :search, '%'))
                or lower(i.itemCode) like lower(concat('%', :search, '%'))
            )
            and (:itemType is null or i.itemType = :itemType)
            """)
    Page<InventoryItem> search(
            @Param("search") String search,
            @Param("itemType") InventoryItemType itemType,
            Pageable pageable
    );
}
