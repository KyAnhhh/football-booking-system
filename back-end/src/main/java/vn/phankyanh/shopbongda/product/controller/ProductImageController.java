package vn.phankyanh.shopbongda.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.dto.request.ProductImageRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductImageResponse;
import vn.phankyanh.shopbongda.product.service.ProductImageService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductImageController {

    private static final int MAX_IMAGES_PER_UPLOAD = 10;
    private static final long MAX_IMAGE_SIZE_BYTES = 2L * 1024 * 1024;

    private final ProductImageService productImageService;

    @PostMapping("/admin/products/{productId}/images")
    public ApiResponse<String> uploadImages(
            @PathVariable Long productId,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam(value = "primaryIndex", required = false) Integer primaryIndex
    ) {
        validateImages(images);

        List<ProductImageRequest> requests = images.stream()
                .map(file -> ProductImageRequest.builder()
                        .image(file)
                        .isPrimary(false)
                        .build())
                .toList();

        if (primaryIndex != null
                && primaryIndex >= 0 
                && primaryIndex < requests.size()) {
            requests.get(primaryIndex).setPrimary(true);
        }

        productImageService.uploadImages(productId, requests);

        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("Upload images successfully");
        return res;
    }

    private void validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("At least one image is required");
        }
        if (images.size() > MAX_IMAGES_PER_UPLOAD) {
            throw new IllegalArgumentException("Maximum 10 images per upload");
        }

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                throw new IllegalArgumentException("Image must not be empty");
            }
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }
            if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
                throw new IllegalArgumentException("Each image must not exceed 2 MB");
            }
        }
    }

    @GetMapping("/public/products/{productId}/images")
    public ApiResponse<List<ProductImageResponse>> getImagesByProduct(
            @PathVariable Long productId) {

        ApiResponse<List<ProductImageResponse>> res = new ApiResponse<>();
        res.setData(productImageService.getImagesByProductId(productId));
        return res;
    }

    @PutMapping("/admin/product-images/{imageId}/primary")
    public ApiResponse<String> setPrimary(@PathVariable Long imageId) {

        productImageService.setPrimary(imageId);

        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("Set primary image successfully");
        return res;
    }

    @DeleteMapping("/admin/product-images/{imageId}")
    public ApiResponse<String> delete(@PathVariable Long imageId) {

        productImageService.delete(imageId);

        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("Delete image successfully");
        return res;
    }
}
