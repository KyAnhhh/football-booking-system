package vn.phankyanh.shopbongda.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Brand;
import vn.phankyanh.shopbongda.dto.request.BrandRequest;
import vn.phankyanh.shopbongda.dto.response.BrandResponse;
import vn.phankyanh.shopbongda.mapper.BrandMapper;
import vn.phankyanh.shopbongda.repository.BrandRepository;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public Page<BrandResponse> handleGetAllBrand(int pageNo, int pageSize, String sortBy, String sortDir, String search){
        Sort.Direction direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        
        Page<Brand> brandsPage;
        if(search != null && !search.isEmpty()){
            brandsPage = brandRepository.findByNameContaining(search, pageable);
        } else {
            brandsPage = brandRepository.findAll(pageable);
        }
        return brandsPage.map(brandMapper::toResponse);
    }
    public BrandResponse handleGetBrandById(Long id){
        Brand brand = this.brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return brandMapper.toResponse(brand);
    }

    public BrandResponse handleCreateBrand(BrandRequest request){
        if(brandRepository.existsByName(request.getName())){
            throw new RuntimeException("Name already exists");
        }
        Brand brand = brandMapper.toBrand(request);
        this.brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    public void handleDeleteBrand(Long id){
        if(!brandRepository.existsById(id)){
            throw new RuntimeException("Not found with id: " + id);
        }
        this.brandRepository.deleteById(id);
    }

    public Brand handleUpdateBrand(Long id, BrandRequest request){

        Brand brand = this.brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        brandMapper.updateBrand(brand, request);
        return brandRepository.save(brand);
    }
}
