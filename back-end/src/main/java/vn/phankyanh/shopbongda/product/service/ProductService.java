package vn.phankyanh.shopbongda.product.service;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse handleCreateProduct(ProductRequest request) {
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

    // Lấy tất cả
    public Page<ProductResponse> handleGetAllProduct(int pageNo, int pageSize, String sortBy, String sortDir,
            String search) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productsPage;
        if (search != null && !search.trim().isEmpty()) {
            productsPage = productRepository.findByNameContaining(search, pageable);
        } else {
            productsPage = productRepository.findAll(pageable);
        }
        return productsPage.map(productMapper::toResponse);
    }

    // Lấy 4 sản phẩm nổi bật và bán chạy nhất
    public List<ProductResponse> handleGetTop4PopularProducts() {
        List<Product> popularProducts = productRepository.findTop4PopularProducts();
        return popularProducts.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    // lấy theo id
    public ProductResponse handleGetProductById(Long id){
        Product product = this.productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
            return productMapper.toResponse(product);
    }


    // update
    @Transactional
    public ProductResponse handleUpdateProduct(Long id, ProductRequest request) {
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

    // xóa
    public void handleDeleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
