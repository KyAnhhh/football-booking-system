package vn.phankyanh.shopbongda.product.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import vn.phankyanh.shopbongda.product.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    Page<ProductResponse> getAllProducts(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            String category,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    List<ProductResponse> getTop4PopularProducts();

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}