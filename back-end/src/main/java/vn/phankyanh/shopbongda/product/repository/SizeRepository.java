package vn.phankyanh.shopbongda.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.product.domain.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    boolean existsByCode(String code);
}
