package vn.phankyanh.shopbongda.field.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import vn.phankyanh.shopbongda.field.service.FootballFieldService;
import vn.phankyanh.shopbongda.maintenance.dto.response.MaintenanceScheduleResponse;
import vn.phankyanh.shopbongda.maintenance.service.MaintenanceService;

@Service
@RequiredArgsConstructor
public class FootballFieldServiceImpl implements FootballFieldService {

    private final BookingRepository bookingRepository;
    private final FootballFieldRepository footballFieldRepository;
    private final FootballFieldMapper footballFieldMapper;
    private final MaintenanceService maintenanceService;

    @Override
    public FootballFieldResponse createFootballField(FootballFieldRequest request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Field name is required");
        }

        if (request.getPricePerHour() == null
                || request.getPricePerHour().compareTo(BigDecimal.ZERO) <= 0) {

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

    @Override
    public Page<FootballFieldResponse> getAllFootballFields(
            String keyword,
            FieldStatus status,
            FieldType type,
            Pageable pageable, 
            BigDecimal minPrice, 
            BigDecimal maxPrice
    ) {

        return footballFieldRepository
                .searchFootballFields(keyword, status, type, minPrice, maxPrice, pageable)
                .map(footballFieldMapper::toResponse);
    }

    @Override
    public FootballFieldResponse getFootballFieldById(Long id) {

        FootballField field = footballFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        return footballFieldMapper.toResponse(field);
    }

    @Override
    public FootballFieldResponse getFootballFieldByIdWithMaintenance(Long id) {
        FootballField field = footballFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        FootballFieldResponse response = footballFieldMapper.toResponse(field);

        MaintenanceScheduleResponse maintenanceStatus = maintenanceService.getMaintenanceStatus(id, LocalDateTime.now());
        response.setUnderMaintenance(maintenanceStatus.isUnderMaintenance());
        response.setMaintenanceStartTime(maintenanceStatus.getMaintenanceStartTime());
        response.setMaintenanceEndTime(maintenanceStatus.getMaintenanceEndTime());
        response.setMaintenanceReason(maintenanceStatus.getMaintenanceReason());

        return response;
    }

    @Override
    public List<FootballFieldResponse> getFootballFieldsByStatus(FieldStatus status) {

        return footballFieldRepository.findByStatus(status)
                .stream()
                .map(footballFieldMapper::toResponse)
                .toList();
    }

    @Override
    public void updateStatus(Long id, FieldStatusUpdateRequest newStatus) {

        FootballField field = footballFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        if (newStatus.getStatus() == null) {
            throw new RuntimeException("Status is required");
        }

        field.setStatus(newStatus.getStatus());

        footballFieldRepository.save(field);
    }

    @Override
    public FootballFieldResponse updateFootballField(
            Long id,
            FootballFieldRequest request
    ) {

        FootballField field = footballFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        if (request.getPricePerHour() != null
                && request.getPricePerHour().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("Price must be greater than 0");
        }

        footballFieldMapper.updateField(field, request);

        FootballField updatedField = footballFieldRepository.save(field);

        return footballFieldMapper.toResponse(updatedField);
    }

    @Override
    public void deleteFootballField(Long id) {

        FootballField field = footballFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        if (bookingRepository.existsByFieldId(id)) {
            throw new RuntimeException("Cannot delete field with bookings");
        }

        footballFieldRepository.delete(field);
    }
}