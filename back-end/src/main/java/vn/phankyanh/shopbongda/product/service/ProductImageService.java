package vn.phankyanh.shopbongda.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.domain.Product;
import vn.phankyanh.shopbongda.product.domain.ProductImage;
import vn.phankyanh.shopbongda.product.dto.request.ProductImageRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductImageResponse;
import vn.phankyanh.shopbongda.product.mapper.ProductImageMapper;
import vn.phankyanh.shopbongda.product.repository.ProductImageRepository;
import vn.phankyanh.shopbongda.product.repository.ProductRepository;
import vn.phankyanh.shopbongda.shared.service.image.ImageService;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;
    private final ProductRepository productRepository;
    private final ImageService imageService;

    @Transactional
    public void uploadImages(Long productId, List<ProductImageRequest> requests) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        boolean hasPrimary = false;

        for (ProductImageRequest req : requests) {

            if (req.isPrimary()) {
                if (hasPrimary) {
                    throw new RuntimeException("Only one primary image allowed");
                }
                hasPrimary = true;
                productImageRepository.resetPrimary(productId);
            }

            String imageUrl = imageService.uploadImage(req.getImage());

            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .isPrimary(req.isPrimary())
                    .build();

            product.getImages().add(productImage);
            
        }
            productRepository.save(product);

    }

    @Transactional
    public void setPrimary(Long imageId) {

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Product image not found"));

        if (Boolean.TRUE.equals(productImage.getIsPrimary())) {
            return; 
        }

        Long productId = productImage.getProduct().getId();

        productImageRepository.resetPrimary(productId);
        productImage.setIsPrimary(true);

    }

    public List<ProductImageResponse> getImagesByProductId(Long productId) {
        return productImageRepository.findByProductId(productId)
                .stream()
                .map(productImageMapper::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if (!productImageRepository.existsById(id)) {
            throw new RuntimeException("Product image not found");
        }
        productImageRepository.deleteById(id);
    }
}