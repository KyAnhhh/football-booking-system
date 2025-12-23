package vn.phankyanh.shopbongda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    
}
