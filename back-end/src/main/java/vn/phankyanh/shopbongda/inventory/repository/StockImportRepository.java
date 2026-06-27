package vn.phankyanh.shopbongda.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.phankyanh.shopbongda.inventory.domain.StockImport;

public interface StockImportRepository extends JpaRepository<StockImport, Long> {
    boolean existsByImportCode(String importCode);
}
