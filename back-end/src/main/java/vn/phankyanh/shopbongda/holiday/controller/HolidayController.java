package vn.phankyanh.shopbongda.holiday.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.phankyanh.shopbongda.holiday.dto.response.HolidayResponse;
import vn.phankyanh.shopbongda.holiday.service.HolidayService;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/public/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public ApiResponse<List<HolidayResponse>> getAll() {
        ApiResponse<List<HolidayResponse>> response = new ApiResponse<>();
        response.setData(holidayService.getAll());
        return response;
    }

    @GetMapping("/{id}")
    public ApiResponse<HolidayResponse> getById(
            @PathVariable Long id) {

        ApiResponse<HolidayResponse> response = new ApiResponse<>();
        response.setData(holidayService.getById(id));
        return response;
    }
}