package vn.phankyanh.shopbongda.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.domain.Size;
import vn.phankyanh.shopbongda.product.dto.request.SizeRequest;
import vn.phankyanh.shopbongda.product.dto.response.SizeResponse;
import vn.phankyanh.shopbongda.product.mapper.SizeMapper;
import vn.phankyanh.shopbongda.product.repository.SizeRepository;

@Service
@RequiredArgsConstructor
public class SizeService {
    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;

    public SizeResponse handleCreateSize(SizeRequest request) {
        Size size = sizeMapper.toSize(request);
        this.sizeRepository.save(size);
        return sizeMapper.toResponse(size);
    }

    public List<SizeResponse> handleGetAllSize(){
    return this.sizeRepository.findAll()
            .stream()
            .map(sizeMapper::toResponse)
            .toList();
    }

    public SizeResponse handleGetSizeById(Long id){
        Size size = this.sizeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Size not found"));
        return sizeMapper.toResponse(size);
    }

    public SizeResponse handleUpdateSize(Long id, SizeRequest request) {
        Size size = this.sizeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Size not found"));
        sizeMapper.updateSize(size, request);
        return sizeMapper.toResponse(this.sizeRepository.save(size));
    }

    public void handleDeleteSize(Long id){
        if(!this.sizeRepository.existsById(id))
            throw new RuntimeException("Size not found");
        this.sizeRepository.deleteById(id);
    }
}
