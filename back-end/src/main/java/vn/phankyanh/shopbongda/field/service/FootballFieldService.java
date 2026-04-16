package vn.phankyanh.shopbongda.field.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FieldType;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.dto.request.FieldStatusUpdateRequest;
import vn.phankyanh.shopbongda.field.dto.request.FootballFieldRequest;
import vn.phankyanh.shopbongda.field.dto.response.FootballFieldResponse;
import vn.phankyanh.shopbongda.field.mapper.FootballFieldMapper;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;

@Service
@RequiredArgsConstructor
public class FootballFieldService {
    private final BookingRepository bookingRepository;
    private final FootballFieldRepository footballFieldRepository;
    private final FootballFieldMapper footballFieldMapper;
    

    public FootballFieldResponse createFootballField(FootballFieldRequest request) {
        if(request.getName() == null || request.getName().isBlank()){
            throw new IllegalArgumentException("Field name is required");
        }

        if (request.getPricePerHour() == null ||
            request.getPricePerHour().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }

        FootballField field = footballFieldMapper.toFootballField(request);

        field.setStatus(FieldStatus.AVAILABLE);

        if (request.getImageUrl() == null || request.getImageUrl().isBlank()) {
            field.setImageUrl("default-field.jpg");
        }

        FootballField savedField = footballFieldRepository.save(field);
        
        return footballFieldMapper.toResponse(savedField);
    }

    public Page<FootballFieldResponse> getAllFootballFields(
                String keyword,
                FieldStatus status,
                FieldType type,
                Pageable pageable
    ) {
        return footballFieldRepository.searchFootballFields(keyword, status, type, pageable)
            .map(footballFieldMapper::toResponse);
    }

    public FootballFieldResponse getFootballFieldById(Long id) {
        FootballField field = footballFieldRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found"));

        return footballFieldMapper.toResponse(field);
    }

    public List<FootballFieldResponse> getFootballFieldsByStatus(FieldStatus status) {
        return footballFieldRepository.findByStatus(status).stream()
            .map(footballFieldMapper::toResponse)
            .toList();
    }

    public void updateStatus(Long id, FieldStatusUpdateRequest newStatus) {
        FootballField field = footballFieldRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found"));

        if (newStatus.getStatus() == null) {
            throw new RuntimeException("Status is required");
        }
        field.setStatus(newStatus.getStatus());
        footballFieldRepository.save(field);
    }

    public FootballFieldResponse updateFootballField(Long id, FootballFieldRequest request) {
        FootballField field = footballFieldRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found"));

        if (request.getPricePerHour() != null &&
            request.getPricePerHour().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }

        footballFieldMapper.updateField(field, request);

        FootballField updatedField = footballFieldRepository.save(field);

        return footballFieldMapper.toResponse(updatedField);
    }

    public void deleteFootballField(Long id){
        FootballField field = footballFieldRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found"));

        if (bookingRepository.existsByFieldId(id)) {
            throw new RuntimeException("Cannot delete field with bookings");
        }
        footballFieldRepository.delete(field);
    }
}
