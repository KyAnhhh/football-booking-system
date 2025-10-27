package vn.phankyanh.shopbongda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.domain.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);
}
