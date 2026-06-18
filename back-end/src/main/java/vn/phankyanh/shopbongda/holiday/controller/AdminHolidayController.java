package vn.phankyanh.shopbongda.holiday.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.phankyanh.shopbongda.holiday.dto.request.HolidayRequest;
import vn.phankyanh.shopbongda.holiday.dto.response.HolidayResponse;
import vn.phankyanh.shopbongda.holiday.service.HolidayService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/holidays")
@RequiredArgsConstructor
public class AdminHolidayController {

    private final HolidayService holidayService;

    @PostMapping
    public ApiResponse<HolidayResponse> create(
            @RequestBody HolidayRequest request) {
        
        ApiResponse<HolidayResponse> response = new ApiResponse<>();
        response.setData(holidayService.create(request));
        response.setMessage("Holiday created successfully");
        return response;

    }

    @PutMapping("/{id}")
    public ApiResponse<HolidayResponse> update(
            @PathVariable Long id,
            @RequestBody HolidayRequest request) {

        ApiResponse<HolidayResponse> response = new ApiResponse<>();
        response.setData(holidayService.update(id, request));
        response.setMessage("Holiday updated successfully");
        return response;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(
            @PathVariable Long id) {

        ApiResponse<String> response = new ApiResponse<>();
        holidayService.delete(id);
        response.setMessage("Holiday deleted successfully");
        return response;
    }
}
