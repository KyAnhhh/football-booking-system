package vn.phankyanh.shopbongda.product.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.phankyanh.shopbongda.exception.ProductNotFoundException;
import vn.phankyanh.shopbongda.product.domain.Brand;
import vn.phankyanh.shopbongda.product.domain.Category;
import vn.phankyanh.shopbongda.product.domain.Product;
import vn.phankyanh.shopbongda.product.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductResponse;
import vn.phankyanh.shopbongda.product.mapper.ProductMapper;
import vn.phankyanh.shopbongda.product.repository.BrandRepository;
import vn.phankyanh.shopbongda.product.repository.CategoryRepository;
import vn.phankyanh.shopbongda.product.repository.ProductRepository;
import vn.phankyanh.shopbongda.product.service.ProductService;
import vn.phankyanh.shopbongda.catalog.domain.PublicationStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("Name already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Product product = productMapper.toProduct(request);

        product.setCategory(category);
        product.setBrand(brand);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public Page<ProductResponse> getAllProducts(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            String category,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productsPage = productRepository.filterProducts(
                PublicationStatus.ACTIVE,
                (search == null || search.isBlank()) ? null : search,
                (category == null || category.isBlank()) ? null : category,
                (brand == null || brand.isBlank()) ? null : brand,
                minPrice,
                maxPrice,
                pageable
        );

        return productsPage.map(productMapper::toResponse);
    }

    @Override
    public Page<ProductResponse> getAdminProducts(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search
    ) {
        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return productRepository.filterAdminProducts(
                search == null || search.isBlank() ? null : search,
                null,
                null,
                null,
                null,
                pageable
        ).map(productMapper::toResponse);
    }

    @Override
    public List<ProductResponse> getTop4PopularProducts() {

        List<Product> popularProducts = productRepository.findTop4PopularProducts();

        return popularProducts.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .filter(item -> item.getPublicationStatus()
                        == PublicationStatus.ACTIVE)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.updateProduct(product, request);

        if (request.getCategoryId() != null) {

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category);
        }

        if (request.getBrandId() != null) {

            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));

            product.setBrand(brand);
        }

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);
    }
}
