package vn.phankyanh.shopbongda.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    public ProductResponse handleCreateProduct(ProductRequest request){
        if(productRepository.existsByName(request.getName())){
            throw new RuntimeException("Name already exists");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Product product = productMapper.toProduct(request);

        // gán để nối mối quan hệ thật
        product.setCategory(category);
        product.setBrand(brand);

        this.productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public List<ProductResponse> handleGetAllProduct(){
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    public ProductResponse handleGetProductById(Long id){
         Product product = this.productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toProductResponse(product);
    }

    public Product handleUpdateProduct(Long id, ProductRequest request){
        Product product = this.productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.updateProduct(product, request);
        return this.productRepository.save(product);
    }

    public void handleDeleteProduct(Long id){
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
