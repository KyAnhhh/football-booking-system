package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.domain.Brand;
import vn.phankyanh.shopbongda.dto.request.BrandRequest;
import vn.phankyanh.shopbongda.dto.response.BrandResponse;
import vn.phankyanh.shopbongda.mapper.BrandMapper;
import vn.phankyanh.shopbongda.repository.BrandRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public List<BrandResponse> handleGetAllBrand(){
        return this.brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponse)
                .toList();

    }
    public BrandResponse handleGetUserById(Long id){
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
