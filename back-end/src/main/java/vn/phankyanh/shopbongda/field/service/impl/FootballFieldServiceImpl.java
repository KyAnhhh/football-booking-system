package vn.phankyanh.shopbongda.field.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import vn.phankyanh.shopbongda.field.dto.response.AvailableFootballFieldResponse;
import vn.phankyanh.shopbongda.field.dto.response.FootballFieldResponse;
import vn.phankyanh.shopbongda.field.mapper.FootballFieldMapper;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.field.service.FootballFieldService;
import vn.phankyanh.shopbongda.maintenance.dto.response.MaintenanceScheduleResponse;
import vn.phankyanh.shopbongda.maintenance.service.MaintenanceService;
import vn.phankyanh.shopbongda.pricing.service.PricingService;

@Service
@RequiredArgsConstructor
public class FootballFieldServiceImpl implements FootballFieldService {

    private static final int SLOT_DURATION_MINUTES = 90;
    private static final LocalTime OPEN_TIME = LocalTime.of(7, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(23, 45);

    private final BookingRepository bookingRepository;
    private final FootballFieldRepository footballFieldRepository;
    private final FootballFieldMapper footballFieldMapper;
    private final MaintenanceService maintenanceService;
    private final PricingService pricingService;

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
    public Page<AvailableFootballFieldResponse> getAvailableFootballFields(
            String keyword,
            FieldType type,
            Pageable pageable,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {

        validateAvailableSearchTime(date, startTime, endTime);

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        return footballFieldRepository
                .searchAvailableFootballFields(
                        keyword,
                        FieldStatus.AVAILABLE,
                        type,
                        minPrice,
                        maxPrice,
                        startDateTime,
                        endDateTime,
                        pageable
                )
                .map(field -> toAvailableResponse(
                        field,
                        date,
                        startTime,
                        endTime,
                        startDateTime,
                        endDateTime
                ));
    }

    private void validateAvailableSearchTime(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {

        if (date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Date and time are required");
        }

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Invalid time range");
        }

        if (startTime.isBefore(OPEN_TIME) || endTime.isAfter(CLOSE_TIME)) {
            throw new IllegalArgumentException("Time must be between 07:00 and 23:45");
        }

        if (Duration.between(startTime, endTime).toMinutes() != SLOT_DURATION_MINUTES) {
            throw new IllegalArgumentException("Time slot must be 90 minutes");
        }

        if (LocalDateTime.of(date, startTime).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot search past time slot");
        }
    }

    private AvailableFootballFieldResponse toAvailableResponse(
            FootballField field,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {

        BigDecimal slotPrice;

        try {
            slotPrice = pricingService.calculatePrice(
                    field.getId(),
                    startDateTime,
                    endDateTime
            );
        } catch (RuntimeException exception) {
            slotPrice = BigDecimal.ZERO;
        }

        return AvailableFootballFieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .type(field.getType().name())
                .imageUrl(field.getImageUrl())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                .address(field.getAddress())
                .description(field.getDescription())
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .slotPrice(slotPrice)
                .available(true)
                .build();
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
