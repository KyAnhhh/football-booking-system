package vn.phankyanh.shopbongda.field.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FieldType;
import vn.phankyanh.shopbongda.field.dto.request.FieldStatusUpdateRequest;
import vn.phankyanh.shopbongda.field.dto.request.FootballFieldRequest;
import vn.phankyanh.shopbongda.field.dto.response.FootballFieldResponse;

public interface FootballFieldService {

    FootballFieldResponse createFootballField(FootballFieldRequest request);

    Page<FootballFieldResponse> getAllFootballFields(
            String keyword,
            FieldStatus status,
            FieldType type,
            Pageable pageable,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    FootballFieldResponse getFootballFieldById(Long id);

    FootballFieldResponse getFootballFieldByIdWithMaintenance(Long id);

    List<FootballFieldResponse> getFootballFieldsByStatus(FieldStatus status);

    void updateStatus(Long id, FieldStatusUpdateRequest newStatus);

    FootballFieldResponse updateFootballField(Long id, FootballFieldRequest request);

    void deleteFootballField(Long id);
}