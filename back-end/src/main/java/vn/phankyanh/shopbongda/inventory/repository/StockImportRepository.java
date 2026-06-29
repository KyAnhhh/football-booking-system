package vn.phankyanh.shopbongda.inventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import vn.phankyanh.shopbongda.inventory.domain.StockImport;
import vn.phankyanh.shopbongda.inventory.domain.StockImportStatus;

public interface StockImportRepository extends JpaRepository<StockImport, Long> {
    boolean existsByImportCode(String importCode);

    @Query("""
            select si from StockImport si
            where (:status is null or si.status = :status)
              and (:supplierId is null or si.supplier.id = :supplierId)
              and (:search is null
                   or lower(si.importCode) like lower(concat('%', :search, '%')))
            """)
    Page<StockImport> search(
            @Param("search") String search,
            @Param("status") StockImportStatus status,
            @Param("supplierId") Long supplierId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "supplier",
            "createdBy",
            "items",
            "items.inventoryItem"
    })
    @Query("select si from StockImport si where si.id = :id")
    Optional<StockImport> findDetailById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "supplier",
            "createdBy",
            "items",
            "items.inventoryItem"
    })
    @Query("select si from StockImport si where si.id = :id")
    Optional<StockImport> findByIdForUpdate(@Param("id") Long id);
}
