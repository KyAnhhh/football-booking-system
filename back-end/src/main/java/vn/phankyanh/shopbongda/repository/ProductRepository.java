package vn.phankyanh.shopbongda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.Product;

@Repository
public interface  ProductRepository extends JpaRepository<Product, Long>{
    boolean existsByName(String name);
}
