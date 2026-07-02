package vn.phankyanh.shopbongda.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.product.domain.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);
    boolean existsByNameIgnoreCase(String name);
    Page<Brand> findByNameContaining(String name, Pageable pageable);
}
