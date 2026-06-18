package vn.phankyanh.shopbongda.extra.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import vn.phankyanh.shopbongda.extra.domain.ExtraService;
import vn.phankyanh.shopbongda.extra.dto.request.ExtraServiceRequest;
import vn.phankyanh.shopbongda.extra.dto.response.ExtraServiceResponse;
import vn.phankyanh.shopbongda.extra.mapper.ExtraServiceMapper;
import vn.phankyanh.shopbongda.extra.repository.ExtraServiceRepository;
import vn.phankyanh.shopbongda.extra.service.ExtraServiceService;

@Service
@RequiredArgsConstructor
public class ExtraServiceServiceImpl
        implements ExtraServiceService {

    private final ExtraServiceRepository extraServiceRepository;

    private final ExtraServiceMapper extraServiceMapper;

    @Override
    public ExtraServiceResponse create(
            ExtraServiceRequest request
    ) {

        if (request.getName() == null
                || request.getName().isBlank()) {

            throw new RuntimeException(
                    "Service name is required");
        }

        if (request.getPrice() == null
                || request.getPrice()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Price must be greater than 0");
        }

        ExtraService service =
                extraServiceMapper.toEntity(request);

        if (service.getIsActive() == null) {
            service.setIsActive(true);
        }

        ExtraService saved =
                extraServiceRepository.save(service);

        return extraServiceMapper.toResponse(saved);
    }

    @Override
    public List<ExtraServiceResponse> getAll() {

        return extraServiceRepository.findAll()
                .stream()
                .map(extraServiceMapper::toResponse)
                .toList();
    }

    @Override
    public ExtraServiceResponse getById(
            Long id
    ) {

        ExtraService service =
                extraServiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extra service not found"));

        return extraServiceMapper.toResponse(service);
    }

    @Override
    public ExtraServiceResponse update(
            Long id,
            ExtraServiceRequest request
    ) {

        ExtraService service =
                extraServiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extra service not found"));

        if (request.getPrice() != null
                && request.getPrice()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Price must be greater than 0");
        }

        extraServiceMapper.updateEntity(
                request,
                service
        );

        ExtraService updated =
                extraServiceRepository.save(service);

        return extraServiceMapper.toResponse(updated);
    }

    @Override
    public void delete(
            Long id
    ) {

        ExtraService service =
                extraServiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extra service not found"));

        extraServiceRepository.delete(service);
    }
}