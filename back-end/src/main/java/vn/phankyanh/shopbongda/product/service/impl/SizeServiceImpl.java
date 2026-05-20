package vn.phankyanh.shopbongda.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.domain.Size;
import vn.phankyanh.shopbongda.product.dto.request.SizeRequest;
import vn.phankyanh.shopbongda.product.dto.response.SizeResponse;
import vn.phankyanh.shopbongda.product.mapper.SizeMapper;
import vn.phankyanh.shopbongda.product.repository.SizeRepository;
import vn.phankyanh.shopbongda.product.service.SizeService;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;

    @Override
    public SizeResponse createSize(SizeRequest request) {

        Size size = sizeMapper.toSize(request);

        Size saved = sizeRepository.save(size);

        return sizeMapper.toResponse(saved);
    }

    @Override
    public List<SizeResponse> getAllSize() {

        return sizeRepository.findAll()
                .stream()
                .map(sizeMapper::toResponse)
                .toList();
    }

    @Override
    public SizeResponse getSizeById(Long id) {

        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found"));

        return sizeMapper.toResponse(size);
    }

    @Override
    public SizeResponse updateSize(Long id, SizeRequest request) {

        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found"));

        sizeMapper.updateSize(size, request);

        Size updated = sizeRepository.save(size);

        return sizeMapper.toResponse(updated);
    }

    @Override
    public void deleteSize(Long id) {

        if (!sizeRepository.existsById(id)) {
            throw new RuntimeException("Size not found");
        }

        sizeRepository.deleteById(id);
    }
}