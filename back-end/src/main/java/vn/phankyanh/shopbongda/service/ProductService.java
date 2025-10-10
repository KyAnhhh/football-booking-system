package vn.phankyanh.shopbongda.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import vn.phankyanh.shopbongda.domain.Product;
import vn.phankyanh.shopbongda.exception.ProductNotFoundException;
import vn.phankyanh.shopbongda.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product handleCreateProduct(Product product){
        return this.productRepository.save(product);
    }

    public List<Product> handleGetAllProduct(){
        return this.productRepository.findAll();
    }

    public Product handleGetProductById(Long id){
        return this.productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product handleUpdateProduct(Long id, Product productUpdate){
        Product product = this.productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(productUpdate.getName());
        product.setDescription(productUpdate.getDescription());
        product.setPrice(productUpdate.getPrice());
        product.setStock(productUpdate.getStock());
        product.setImageUrl(productUpdate.getImageUrl());
        product.setCategory(productUpdate.getCategory());

        
        return this.productRepository.save(product);
    }

    public void handleDeleteProduct(Long id){
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
