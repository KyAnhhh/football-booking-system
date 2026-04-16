package vn.phankyanh.shopbongda.field.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FieldType;
import vn.phankyanh.shopbongda.field.dto.request.FieldStatusUpdateRequest;
import vn.phankyanh.shopbongda.field.dto.request.FootballFieldRequest;
import vn.phankyanh.shopbongda.field.dto.response.FootballFieldResponse;
import vn.phankyanh.shopbongda.field.service.FootballFieldService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;


@RestController
@RequiredArgsConstructor
public class FootballFieldController {
    private final FootballFieldService footballFieldService;

    @GetMapping("/api/public/fields")
    public ApiResponse<Page<FootballFieldResponse>> getAllFootballFields(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) FieldStatus status,
        @RequestParam(required = false) FieldType type
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FootballFieldResponse> result = footballFieldService.getAllFootballFields(keyword, FieldStatus.AVAILABLE, type, pageable);

        ApiResponse<Page<FootballFieldResponse>> response = new ApiResponse<>();
        response.setData(result);
        return response;
    }
    
    @PostMapping("/api/admin/fields")
    public ApiResponse<FootballFieldResponse> createFootballField(@RequestBody FootballFieldRequest request) {
        ApiResponse<FootballFieldResponse> response = new ApiResponse<>();
        response.setData(this.footballFieldService.createFootballField(request));
        return response;
    }

    @GetMapping("/api/public/fields/{id}")
    public ApiResponse<FootballFieldResponse> getFootballFieldById(@PathVariable("id") Long id) {
        ApiResponse<FootballFieldResponse> response = new ApiResponse<>();
        response.setData(this.footballFieldService.getFootballFieldById(id));
        return response;
    }

    @PutMapping("/api/admin/fields/{id}/status")
    public ApiResponse<Void> updateFootballFieldStatus(
        @PathVariable("id") Long id,
        @RequestBody FieldStatusUpdateRequest status
    ) {
        ApiResponse<Void> response = new ApiResponse<>();
        this.footballFieldService.updateStatus(id, status);
        response.setData(null);
        response.setMessage("Field status updated successfully");
        return response;
    }

    @PutMapping("/api/admin/fields/{id}")
    public ApiResponse<FootballFieldResponse> updateFootballField(
        @PathVariable("id") Long id,
        @RequestBody FootballFieldRequest request
    ) {
        ApiResponse<FootballFieldResponse> response = new ApiResponse<>();
        response.setData(this.footballFieldService.updateFootballField(id, request));
        return response;
    }

    @DeleteMapping("/api/admin/fields/{id}")
    public ApiResponse<Void> deleteFootballField(@PathVariable("id") Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        this.footballFieldService.deleteFootballField(id);
        response.setData(null);
        response.setMessage("Field deleted successfully");
        return response;
    }
}
