package vn.phankyanh.shopbongda.product.service;

import java.util.List;

import vn.phankyanh.shopbongda.product.dto.request.ProductVariantRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductVariantResponse;

public interface ProductVariantService {

    ProductVariantResponse createProductVariant(ProductVariantRequest request);

    ProductVariantResponse updateProductVariant(Long id, ProductVariantRequest request);

    List<ProductVariantResponse> getAllProductVariant();

    ProductVariantResponse getProductVariantById(Long id);

    void deleteProductVariant(Long id);
}