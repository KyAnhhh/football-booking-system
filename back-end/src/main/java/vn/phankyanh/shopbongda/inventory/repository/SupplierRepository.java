package vn.phankyanh.shopbongda.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.phankyanh.shopbongda.inventory.domain.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByNameIgnoreCase(String name);

    Optional<Supplier> findByNameIgnoreCase(String name);
}
