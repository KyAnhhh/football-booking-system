package vn.phankyanh.shopbongda.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.phankyanh.shopbongda.domain.Brand;
import vn.phankyanh.shopbongda.domain.Category;
import vn.phankyanh.shopbongda.domain.Product;
import vn.phankyanh.shopbongda.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.dto.response.ProductResponse;
import vn.phankyanh.shopbongda.exception.ProductNotFoundException;
import vn.phankyanh.shopbongda.mapper.ProductMapper;
import vn.phankyanh.shopbongda.repository.BrandRepository;
import vn.phankyanh.shopbongda.repository.CategoryRepository;
import vn.phankyanh.shopbongda.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;
    private final ImageService imageService;

    public ProductResponse handleCreateProduct(ProductRequest request){
        if(productRepository.existsByName(request.getName())){
            throw new RuntimeException("Name already exists");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

            // upload ảnh nếu có file, nếu không thì lấy imageUrl từ request
            String imageUrl = null;
            if (request.getImage() != null) {
                try {
                    if (!request.getImage().isEmpty()) {
                        log.info("Received multipart image: name={}, size={}", request.getImage().getOriginalFilename(), request.getImage().getSize());
                        imageUrl = imageService.uploadImage(request.getImage()); // return URL
                    } else {
                        log.info("Multipart image provided but empty");
                    }
                } catch (Exception e) {
                    log.error("Image upload failed", e);
                    throw e;
                }
            }

            if (imageUrl == null && request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
                log.info("Using imageUrl from request payload");
                imageUrl = request.getImageUrl();
        }

        Product product = productMapper.toProduct(request);

        // gán để nối mối quan hệ thật
        product.setCategory(category);
        product.setBrand(brand);
        product.setImageUrl(imageUrl);
        log.info(imageUrl);

        this.productRepository.save(product);
        return productMapper.toProductResponse(product);
    }
    
    public Page<ProductResponse> handleGetAllProduct(int pageNo, int pageSize, String sortBy, String sortDir, String search){
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
        return productsPage.map(productMapper::toProductResponse);
    }

    public ProductResponse handleGetProductById(Long id){
         Product product = this.productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toProductResponse(product);
    }

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

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String newImageUrl = imageService.uploadImage(request.getImage());
            product.setImageUrl(newImageUrl);
        }

        Product updatedProduct = productRepository.save(product);

        return productMapper.toProductResponse(updatedProduct);
    }

    public void handleDeleteProduct(Long id){
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
