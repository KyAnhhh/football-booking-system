package vn.phankyanh.shopbongda.holiday.service;

import java.time.LocalDate;
import java.util.List;

import vn.phankyanh.shopbongda.holiday.dto.request.HolidayRequest;
import vn.phankyanh.shopbongda.holiday.dto.response.HolidayResponse;

public interface HolidayService {
    HolidayResponse create(HolidayRequest request);

    List<HolidayResponse> getAll();

    HolidayResponse getById(Long id);

    HolidayResponse update(Long id, HolidayRequest request);

    void delete(Long id);

    boolean isHoliday(LocalDate date);
}
