package vn.phankyanh.shopbongda.field.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.service.BookingService;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FieldType;
import vn.phankyanh.shopbongda.field.dto.request.FieldStatusUpdateRequest;
import vn.phankyanh.shopbongda.field.dto.request.FootballFieldRequest;
import vn.phankyanh.shopbongda.field.dto.response.FootballFieldResponse;
import vn.phankyanh.shopbongda.field.dto.response.SlotResponse;
import vn.phankyanh.shopbongda.field.service.FootballFieldService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FootballFieldController {
    private final FootballFieldService footballFieldService;
    private final BookingService bookingService;

    @GetMapping("/public/fields")
    public ApiResponse<Page<FootballFieldResponse>> getAllFootballFields(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) FieldStatus status,
        @RequestParam(required = false) FieldType type,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FootballFieldResponse> result = footballFieldService.getAllFootballFields(keyword, FieldStatus.AVAILABLE, type, pageable, minPrice, maxPrice);

        ApiResponse<Page<FootballFieldResponse>> response = new ApiResponse<>();
        response.setData(result);
        return response;
    }
    
    @PostMapping("/admin/fields")
    public ApiResponse<FootballFieldResponse> createFootballField(@RequestBody FootballFieldRequest request) {
        ApiResponse<FootballFieldResponse> response = new ApiResponse<>();
        response.setData(this.footballFieldService.createFootballField(request));
        return response;
    } 

    @GetMapping("/public/fields/{id}")
    public ApiResponse<FootballFieldResponse> getFootballFieldById(@PathVariable("id") Long id) {
        ApiResponse<FootballFieldResponse> response = new ApiResponse<>();
        response.setData(this.footballFieldService.getFootballFieldByIdWithMaintenance(id));
        return response;
    }

    @GetMapping("/public/fields/{fieldId}/slots")
    public ApiResponse<List<SlotResponse>> getAvailableSlots(
        @PathVariable("fieldId") Long fieldId,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date
    ) {
        ApiResponse<List<SlotResponse>> response = new ApiResponse<>();
        response.setData(bookingService.getAvailableSlots(fieldId, date));
        return response;
    }


    @PutMapping("/admin/fields/{id}/status")
    public ApiResponse<Void> updateFootballFieldStatus(
        @PathVariable("id") Long id,
        @RequestBody FieldStatusUpdateRequest status
    ) {
        ApiResponse<Void> response = new ApiResponse<>();
        this.footballFieldService.updateStatus(id, status);
        response.setData(null);
        return response;
    }

    @PutMapping("/admin/fields/{id}")
    public ApiResponse<FootballFieldResponse> updateFootballField(
        @PathVariable("id") Long id,
        @RequestBody FootballFieldRequest request
    ) {
        ApiResponse<FootballFieldResponse> response = new ApiResponse<>();
        response.setData(this.footballFieldService.updateFootballField(id, request));
        return response;
    }

    @DeleteMapping("/admin/fields/{id}")
    public ApiResponse<Void> deleteFootballField(@PathVariable("id") Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        this.footballFieldService.deleteFootballField(id);
        response.setData(null);
        return response;
    }
}
