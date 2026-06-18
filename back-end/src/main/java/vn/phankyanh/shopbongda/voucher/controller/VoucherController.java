package vn.phankyanh.shopbongda.voucher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import  org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import vn.phankyanh.shopbongda.voucher.dto.response.VoucherResponse;
import vn.phankyanh.shopbongda.voucher.service.VoucherService;
import java.math.BigDecimal;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/public/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/apply")
    public ApiResponse<BigDecimal> applyVoucher(
            @RequestParam String code,
            @RequestParam BigDecimal amount
    ) {
        ApiResponse<BigDecimal> response = new ApiResponse<>();
        response.setData(voucherService.applyVoucher(
                code,
                amount
        ));
        response.setMessage("Apply voucher successfully");

        return response;
    }

    @GetMapping
    public ApiResponse<List<VoucherResponse>> getAll() {

        ApiResponse<List<VoucherResponse>> response = new ApiResponse<>();
        response.setData(voucherService.getAll());
        response.setMessage("Get all voucher successfully");

        return response;
    }

    @GetMapping("/{id}")
    public ApiResponse<VoucherResponse> getById(
            @PathVariable Long id
    ) {
        ApiResponse<VoucherResponse> response = new ApiResponse<>();
        response.setData(voucherService.getById(id));
        response.setMessage("Get voucher successfully");

        return response;
    }

    @GetMapping("/code/{code}")
    public ApiResponse<VoucherResponse> getByCode(
            @PathVariable String code
    ) {
        ApiResponse<VoucherResponse> response = new ApiResponse<>();
        response.setData(voucherService.getByCode(code));
        response.setMessage("Get voucher by code successfully");

        return response;
    }
}