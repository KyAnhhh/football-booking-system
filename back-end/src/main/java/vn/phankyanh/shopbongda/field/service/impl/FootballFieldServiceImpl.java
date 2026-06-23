package vn.phankyanh.shopbongda.field.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FieldType;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.domain.SuggestionType;
import vn.phankyanh.shopbongda.field.dto.request.FieldStatusUpdateRequest;
import vn.phankyanh.shopbongda.field.dto.request.FootballFieldRequest;
import vn.phankyanh.shopbongda.field.dto.response.AvailableFootballFieldResponse;
import vn.phankyanh.shopbongda.field.dto.response.FieldSuggestionResponse;
import vn.phankyanh.shopbongda.field.dto.response.FootballFieldResponse;
import vn.phankyanh.shopbongda.field.dto.response.SuggestedFieldSlotResponse;
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

    @Override
    public FieldSuggestionResponse suggestFields(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            FieldType fieldType,
            Long fieldId
    ) {
        validateAvailableSearchTime(date, startTime, endTime);

        List<SuggestedFieldSlotResponse> candidates = new ArrayList<>();

        List<FootballField> exactFields = findAvailableFields(
                date,
                startTime,
                endTime,
                fieldType,
                20
        );

        exactFields.forEach(field -> addSuggestion(
                candidates,
                field,
                date,
                startTime,
                endTime,
                SuggestionType.EXACT_MATCH,
                fieldId,
                fieldType,
                startTime,
                "Sân còn trống đúng khung giờ bạn chọn"
        ));

        findAvailableFields(date, startTime, endTime, fieldType, 20)
                .stream()
                .filter(field -> fieldId == null || !field.getId().equals(fieldId))
                .forEach(field -> addSuggestion(
                        candidates,
                        field,
                        date,
                        startTime,
                        endTime,
                        SuggestionType.SAME_TIME_ALTERNATIVE_FIELD,
                        fieldId,
                        fieldType,
                        startTime,
                        "Sân cùng loại còn trống đúng giờ bạn chọn"
                ));

        if (fieldType != null) {
            findAvailableFields(date, startTime, endTime, null, 30)
                    .stream()
                    .filter(field -> !field.getType().equals(fieldType))
                    .forEach(field -> addSuggestion(
                            candidates,
                            field,
                            date,
                            startTime,
                            endTime,
                            SuggestionType.DIFFERENT_TYPE_SAME_TIME,
                            fieldId,
                            fieldType,
                            startTime,
                            "Sân khác loại còn trống đúng giờ bạn chọn"
                    ));
        }

        for (LocalTime nearbyStart : buildNearbyStartTimes(date, startTime)) {
            LocalTime nearbyEnd = nearbyStart.plusMinutes(SLOT_DURATION_MINUTES);

            if (fieldId != null) {
                findAvailableFields(date, nearbyStart, nearbyEnd, null, 30)
                        .stream()
                        .filter(field -> field.getId().equals(fieldId))
                        .findFirst()
                        .ifPresent(field -> addSuggestion(
                                candidates,
                                field,
                                date,
                                nearbyStart,
                                nearbyEnd,
                                SuggestionType.SAME_FIELD_NEARBY_TIME,
                                fieldId,
                                fieldType,
                                startTime,
                                "Chính sân này còn trống ở khung giờ gần nhất"
                        ));
            }

            findAvailableFields(date, nearbyStart, nearbyEnd, fieldType, 20)
                    .forEach(field -> addSuggestion(
                            candidates,
                            field,
                            date,
                            nearbyStart,
                            nearbyEnd,
                            SuggestionType.SAME_TYPE_NEARBY_TIME,
                            fieldId,
                            fieldType,
                            startTime,
                            "Sân cùng loại còn trống ở khung giờ gần nhất"
                    ));

            if (fieldType != null) {
                findAvailableFields(date, nearbyStart, nearbyEnd, null, 30)
                        .stream()
                        .filter(field -> !field.getType().equals(fieldType))
                        .forEach(field -> addSuggestion(
                                candidates,
                                field,
                                date,
                                nearbyStart,
                                nearbyEnd,
                                SuggestionType.DIFFERENT_TYPE_NEARBY_TIME,
                                fieldId,
                                fieldType,
                                startTime,
                                "Sân khác loại còn trống ở khung giờ gần nhất"
                        ));
            }
        }

        List<SuggestedFieldSlotResponse> suggestions = deduplicateAndRank(candidates)
                .stream()
                .limit(10)
                .toList();

        return FieldSuggestionResponse.builder()
                .requestedDate(date)
                .requestedStartTime(startTime)
                .requestedEndTime(endTime)
                .requestedFieldId(fieldId)
                .requestedFieldType(fieldType == null ? null : fieldType.name())
                .hasExactAvailable(!exactFields.isEmpty())
                .suggestions(suggestions)
                .build();
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

    private List<FootballField> findAvailableFields(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            FieldType fieldType,
            int limit
    ) {
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by("id").ascending()
        );

        return footballFieldRepository
                .searchAvailableFootballFields(
                        null,
                        FieldStatus.AVAILABLE,
                        fieldType,
                        null,
                        null,
                        startDateTime,
                        endDateTime,
                        pageable
                )
                .getContent();
    }

    private List<LocalTime> buildNearbyStartTimes(
            LocalDate date,
            LocalTime requestedStartTime
    ) {
        return List.of(-180, -90, 90, 180)
                .stream()
                .map(requestedStartTime::plusMinutes)
                .filter(candidateStart -> !candidateStart.isBefore(OPEN_TIME))
                .filter(candidateStart -> !candidateStart.plusMinutes(SLOT_DURATION_MINUTES).isAfter(CLOSE_TIME))
                .filter(candidateStart -> !LocalDateTime.of(date, candidateStart).isBefore(LocalDateTime.now()))
                .toList();
    }

    private void addSuggestion(
            List<SuggestedFieldSlotResponse> candidates,
            FootballField field,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            SuggestionType type,
            Long requestedFieldId,
            FieldType requestedFieldType,
            LocalTime requestedStartTime,
            String reason
    ) {
        buildSuggestion(
                field,
                date,
                startTime,
                endTime,
                type,
                requestedFieldId,
                requestedFieldType,
                requestedStartTime,
                reason
        ).ifPresent(candidates::add);
    }

    private Optional<SuggestedFieldSlotResponse> buildSuggestion(
            FootballField field,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            SuggestionType type,
            Long requestedFieldId,
            FieldType requestedFieldType,
            LocalTime requestedStartTime,
            String reason
    ) {
        BigDecimal price;
        try {
            price = pricingService.calculatePrice(
                    field.getId(),
                    LocalDateTime.of(date, startTime),
                    LocalDateTime.of(date, endTime)
            );
        } catch (RuntimeException exception) {
            return Optional.empty();
        }

        return Optional.of(SuggestedFieldSlotResponse.builder()
                .type(type)
                .fieldId(field.getId())
                .fieldName(field.getName())
                .fieldType(field.getType().name())
                .imageUrl(field.getImageUrl())
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .price(price)
                .score(calculateSuggestionScore(
                        field,
                        type,
                        requestedFieldId,
                        requestedFieldType,
                        requestedStartTime,
                        startTime
                ))
                .reason(reason)
                .build());
    }

    private Integer calculateSuggestionScore(
            FootballField field,
            SuggestionType type,
            Long requestedFieldId,
            FieldType requestedFieldType,
            LocalTime requestedStartTime,
            LocalTime candidateStartTime
    ) {
        int score = switch (type) {
            case EXACT_MATCH -> 100;
            case SAME_TIME_ALTERNATIVE_FIELD -> 90;
            case SAME_FIELD_NEARBY_TIME -> 80;
            case SAME_TYPE_NEARBY_TIME -> 70;
            case DIFFERENT_TYPE_SAME_TIME -> 50;
            case DIFFERENT_TYPE_NEARBY_TIME -> 40;
        };

        if (requestedFieldType != null && field.getType().equals(requestedFieldType)) {
            score += 10;
        }
        if (requestedFieldId != null && field.getId().equals(requestedFieldId)) {
            score += 10;
        }

        long timeDistance = Math.abs(Duration.between(requestedStartTime, candidateStartTime).toMinutes());
        score -= (int) (timeDistance / SLOT_DURATION_MINUTES) * 5;

        return score;
    }

    private List<SuggestedFieldSlotResponse> deduplicateAndRank(
            List<SuggestedFieldSlotResponse> candidates
    ) {
        Map<String, SuggestedFieldSlotResponse> deduped = new LinkedHashMap<>();

        for (SuggestedFieldSlotResponse candidate : candidates) {
            String key = candidate.getFieldId()
                    + "|"
                    + candidate.getDate()
                    + "|"
                    + candidate.getStartTime()
                    + "|"
                    + candidate.getEndTime();

            SuggestedFieldSlotResponse existing = deduped.get(key);
            if (existing == null || candidate.getScore() > existing.getScore()) {
                deduped.put(key, candidate);
            }
        }

        return deduped.values()
                .stream()
                .sorted(Comparator
                        .comparing(SuggestedFieldSlotResponse::getScore).reversed()
                        .thenComparing(SuggestedFieldSlotResponse::getPrice)
                        .thenComparing(SuggestedFieldSlotResponse::getStartTime)
                        .thenComparing(SuggestedFieldSlotResponse::getFieldId))
                .toList();
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
