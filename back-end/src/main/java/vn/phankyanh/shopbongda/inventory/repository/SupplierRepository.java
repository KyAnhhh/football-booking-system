package vn.phankyanh.shopbongda.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.phankyanh.shopbongda.inventory.domain.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
