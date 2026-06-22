package vn.phankyanh.shopbongda.pricing.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.pricing.dto.request.HolidayPricingRuleRequest;
import vn.phankyanh.shopbongda.pricing.dto.response.HolidayPricingRuleResponse;
import vn.phankyanh.shopbongda.pricing.service.HolidayPricingRuleService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/holiday-pricing-rules")
@RequiredArgsConstructor
public class HolidayPricingRuleController {

    private final HolidayPricingRuleService holidayPricingRuleService;

    @GetMapping
    public ApiResponse<List<HolidayPricingRuleResponse>> getAll(
            @RequestParam(required = false) Long fieldId,
            @RequestParam(required = false) Long holidayId
    ) {
        ApiResponse<List<HolidayPricingRuleResponse>> response = new ApiResponse<>();
        response.setData(holidayPricingRuleService.getAll(fieldId, holidayId));
        response.setMessage("Get holiday pricing rules successfully");
        return response;
    }

    @PostMapping
    public ApiResponse<HolidayPricingRuleResponse> create(
            @Valid @RequestBody HolidayPricingRuleRequest request
    ) {
        ApiResponse<HolidayPricingRuleResponse> response = new ApiResponse<>();
        response.setData(holidayPricingRuleService.create(request));
        response.setMessage("Create holiday pricing rule successfully");
        return response;
    }

    @PutMapping("/{id}")
    public ApiResponse<HolidayPricingRuleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HolidayPricingRuleRequest request
    ) {
        ApiResponse<HolidayPricingRuleResponse> response = new ApiResponse<>();
        response.setData(holidayPricingRuleService.update(id, request));
        response.setMessage("Update holiday pricing rule successfully");
        return response;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        ApiResponse<String> response = new ApiResponse<>();
        holidayPricingRuleService.delete(id);
        response.setData(null);
        response.setMessage("Delete holiday pricing rule successfully");
        return response;
    }
}
