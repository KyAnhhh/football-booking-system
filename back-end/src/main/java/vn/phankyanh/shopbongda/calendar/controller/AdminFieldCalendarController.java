package vn.phankyanh.shopbongda.calendar.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.calendar.dto.response.AdminFieldCalendarResponse;
import vn.phankyanh.shopbongda.calendar.service.AdminFieldCalendarService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/calendar")
public class AdminFieldCalendarController {

    private final AdminFieldCalendarService adminFieldCalendarService;

    @GetMapping("/fields")
    public ApiResponse<AdminFieldCalendarResponse> getFieldCalendar(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {

        ApiResponse<AdminFieldCalendarResponse> response =
                new ApiResponse<>();

        response.setData(
                adminFieldCalendarService.getFieldCalendar(date)
        );

        return response;
    }
}
