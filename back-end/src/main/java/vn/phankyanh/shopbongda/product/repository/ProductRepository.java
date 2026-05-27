package vn.phankyanh.shopbongda.product.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.product.domain.Product;

@Repository
public interface  ProductRepository extends JpaRepository<Product, Long>{
    boolean existsByName(String name);
    Page<Product> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p")
    Long countProducts();

    @Query("""
    SELECT DISTINCT p FROM Product p
    JOIN p.variants v
    WHERE
    (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
    AND (:category IS NULL OR p.category.name = :category)
    AND (:brand IS NULL OR p.brand.name = :brand)
    AND (:minPrice IS NULL OR v.price >= :minPrice)
    AND (:maxPrice IS NULL OR v.price <= :maxPrice)
    """)
    Page<Product> filterProducts(
            @Param("search") String search,
            @Param("category") String category,
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    // Lấy 5 sản phẩm nổi bật và bán chạy nhất
    @Query(value = """
        SELECT p.* 
        FROM products p
        LEFT JOIN product_variants pv ON p.id = pv.product_id
        GROUP BY p.id
        ORDER BY COALESCE(SUM(pv.sold_quantity), 0) DESC, p.id DESC
        LIMIT 4
    """, nativeQuery = true)
    List<Product> findTop4PopularProducts();

}
