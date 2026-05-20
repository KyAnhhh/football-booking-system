package vn.phankyanh.shopbongda.product.service;

import org.springframework.data.domain.Page;

import vn.phankyanh.shopbongda.product.dto.request.BrandRequest;
import vn.phankyanh.shopbongda.product.dto.response.BrandResponse;

public interface BrandService {

    Page<BrandResponse> getAllBrands(int pageNo, int pageSize, String sortBy, String sortDir, String search);

    BrandResponse getBrandById(Long id);

    BrandResponse createBrand(BrandRequest request);

    BrandResponse updateBrand(Long id, BrandRequest request);

    void deleteBrand(Long id);
}