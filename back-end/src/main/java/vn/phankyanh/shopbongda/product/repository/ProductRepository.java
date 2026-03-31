package vn.phankyanh.shopbongda.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.product.domain.Product;

@Repository
public interface  ProductRepository extends JpaRepository<Product, Long>{
    boolean existsByName(String name);
    Page<Product> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p")
    Long countProducts();

}
