package vn.phankyanh.shopbongda.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.domain.Product;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.domain.Size;
import vn.phankyanh.shopbongda.product.dto.request.ProductVariantRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductVariantResponse;
import vn.phankyanh.shopbongda.product.mapper.ProductVariantMapper;
import vn.phankyanh.shopbongda.product.repository.ProductRepository;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.product.repository.SizeRepository;

@Service
@RequiredArgsConstructor
public class ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final ProductVariantMapper variantMapper;

    // create
    @Transactional
    public ProductVariantResponse handleCreateProductVariant(ProductVariantRequest request){
        if(productVariantRepository.existsByProductIdAndSizeId(request.getProductId(), request.getSizeId())) {
            throw new RuntimeException("Product variant already exists");
        }
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        ProductVariant productVariant = this.variantMapper.toCreateProductVariant(request);

        productVariant.setProduct(product);
        productVariant.setSize(size);

        product.getVariants().add(productVariant);

        productRepository.save(product);

        // refresh: fetch the saved variant to ensure id and other generated fields are populated
        ProductVariant saved = this.productVariantRepository
            .findByProductIdAndSizeId(request.getProductId(), request.getSizeId())
            .orElseThrow(() -> new RuntimeException("Failed to retrieve saved product variant"));

        return this.variantMapper.toResponse(saved);
    }
    //update
    @Transactional
    public ProductVariantResponse handleUpdateProductVariant(Long id, ProductVariantRequest request) {
        ProductVariant productVariant = this.productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        ProductVariant updatedProductVariant = this.variantMapper.updateProductVariant(productVariant, request);
        return this.variantMapper.toResponse(this.productVariantRepository.save(updatedProductVariant));
    }

    //get all
    public List<ProductVariantResponse> handleGetAllProductVariant(){
        return this.productVariantRepository.findAll()
            .stream()
            .map(this.variantMapper::toResponse)
            .toList();
    }

    // get by id
    public ProductVariantResponse handleGetProductVariantById(Long id) {
        ProductVariant productVariant = this.productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found"));
        return this.variantMapper.toResponse(productVariant);
    }

    // delete
    public void handDeleteProductVariant(Long id) {
        if(!productVariantRepository.existsById(id)) {
            throw new RuntimeException("Product variant not found");
        }
        this.productVariantRepository.deleteById(id);
    }

}
