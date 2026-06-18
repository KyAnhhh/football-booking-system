package vn.phankyanh.shopbongda.pricing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;
import vn.phankyanh.shopbongda.pricing.dto.request.FieldPricingRuleRequest;
import vn.phankyanh.shopbongda.pricing.dto.response.FieldPricingRuleResponse;
import vn.phankyanh.shopbongda.pricing.service.FieldPricingRuleService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;


@RestController
@RequestMapping("/api/admin/pricing-rules")
@RequiredArgsConstructor
public class FieldPricingRuleController {

    private final FieldPricingRuleService fieldPricingRuleService;

    @PostMapping
    public ApiResponse<FieldPricingRuleResponse> create(
            @Valid @RequestBody FieldPricingRuleRequest request) {

        ApiResponse<FieldPricingRuleResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(fieldPricingRuleService.create(request));
        apiResponse.setMessage("Create field pricing rule successfully");
        return apiResponse;
    }

    @GetMapping("/field/{fieldId}")
    public ApiResponse<List<FieldPricingRuleResponse>> getByField(
            @PathVariable Long fieldId) {

        ApiResponse<List<FieldPricingRuleResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(fieldPricingRuleService.getByField(fieldId));
        apiResponse.setMessage("Get by field successfully");
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<FieldPricingRuleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FieldPricingRuleRequest request) {
        ApiResponse<FieldPricingRuleResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(fieldPricingRuleService.update(id, request));
        apiResponse.setMessage("Update successfully");
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(
            @PathVariable Long id) {

        ApiResponse<String> apiResponse = new ApiResponse<>();
        fieldPricingRuleService.delete(id);
        apiResponse.setData(null);
        apiResponse.setMessage("Deleted successfully");
        return apiResponse;

    }
}
