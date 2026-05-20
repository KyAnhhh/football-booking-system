package vn.phankyanh.shopbongda.product.service;

import java.util.List;

import vn.phankyanh.shopbongda.product.dto.request.ProductImageRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductImageResponse;

public interface ProductImageService {

    void uploadImages(Long productId, List<ProductImageRequest> requests);

    void setPrimary(Long imageId);

    List<ProductImageResponse> getImagesByProductId(Long productId);

    void delete(Long id);
}