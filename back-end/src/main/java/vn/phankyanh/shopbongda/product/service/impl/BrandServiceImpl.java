package vn.phankyanh.shopbongda.product.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.domain.Brand;
import vn.phankyanh.shopbongda.product.dto.request.BrandRequest;
import vn.phankyanh.shopbongda.product.dto.response.BrandResponse;
import vn.phankyanh.shopbongda.product.mapper.BrandMapper;
import vn.phankyanh.shopbongda.product.repository.BrandRepository;
import vn.phankyanh.shopbongda.product.service.BrandService;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public Page<BrandResponse> getAllBrands(int pageNo, int pageSize, String sortBy, String sortDir, String search) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));

        Page<Brand> brandsPage = (search != null && !search.isEmpty())
                ? brandRepository.findByNameContaining(search, pageable)
                : brandRepository.findAll(pageable);

        return brandsPage.map(brandMapper::toResponse);
    }

    @Override
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found: " + id));

        return brandMapper.toResponse(brand);
    }

    @Override
    public BrandResponse createBrand(BrandRequest request) {

        if (brandRepository.existsByName(request.getName())) {
            throw new RuntimeException("Brand name already exists");
        }

        Brand brand = brandMapper.toBrand(request);
        Brand saved = brandRepository.save(brand);

        return brandMapper.toResponse(saved);
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandRequest request) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found: " + id));

        brandMapper.updateBrand(brand, request);

        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    public void deleteBrand(Long id) {

        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found: " + id);
        }

        brandRepository.deleteById(id);
    }
}