package vn.phankyanh.shopbongda.calendar.service;

import java.time.LocalDate;

import vn.phankyanh.shopbongda.calendar.dto.response.AdminFieldCalendarResponse;

public interface AdminFieldCalendarService {
    AdminFieldCalendarResponse getFieldCalendar(LocalDate date);
}
