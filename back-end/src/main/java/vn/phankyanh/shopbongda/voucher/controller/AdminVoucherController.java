package vn.phankyanh.shopbongda.voucher.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import vn.phankyanh.shopbongda.voucher.dto.request.VoucherRequest;
import vn.phankyanh.shopbongda.voucher.dto.response.VoucherResponse;
import vn.phankyanh.shopbongda.voucher.service.VoucherService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/vouchers")
@RequiredArgsConstructor
public class AdminVoucherController {

    private final VoucherService voucherService;

    @PostMapping
    public ApiResponse<VoucherResponse> create(
            @RequestBody VoucherRequest request
    ) {
        ApiResponse<VoucherResponse> response = new ApiResponse<>();
        response.setData(voucherService.create(request));
        response.setMessage("Apply voucher successfully");

        return response;
    }

    @PutMapping("/{id}")
    public ApiResponse<VoucherResponse> update(
            @PathVariable Long id,
            @RequestBody VoucherRequest request
    ) {
        ApiResponse<VoucherResponse> response = new ApiResponse<>();
        response.setData(voucherService.update(id, request));
        response.setMessage("Update voucher successfully");

        return response;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(
            @PathVariable Long id
    ) {
        ApiResponse<String> response = new ApiResponse<>();
        voucherService.delete(id);
        response.setData(null);
        response.setMessage("Apply voucher successfully");

        return response;
    }
}