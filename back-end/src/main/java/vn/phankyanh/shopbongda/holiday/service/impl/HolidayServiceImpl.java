package vn.phankyanh.shopbongda.holiday.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import vn.phankyanh.shopbongda.holiday.domain.Holiday;
import vn.phankyanh.shopbongda.holiday.dto.request.HolidayRequest;
import vn.phankyanh.shopbongda.holiday.dto.response.HolidayResponse;
import vn.phankyanh.shopbongda.holiday.mapper.HolidayMapper;
import vn.phankyanh.shopbongda.holiday.repository.HolidayRepository;
import vn.phankyanh.shopbongda.holiday.service.HolidayService;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;

    @Override
    public HolidayResponse create(HolidayRequest request) {

        if (holidayRepository.existsByHolidayDateAndIsActiveTrue(
                request.getHolidayDate())) {

            throw new IllegalArgumentException(
                    "Holiday date already exists");
        }

        Holiday holiday = holidayMapper.toEntity(request);

        holiday.setIsActive(true);

        return holidayMapper.toResponse(
                holidayRepository.save(holiday));
    }

    @Override
    public List<HolidayResponse> getAll() {

        return holidayRepository.findByIsActiveTrue()
                .stream()
                .map(holidayMapper::toResponse)
                .toList();
    }

    @Override
    public HolidayResponse getById(Long id) {

        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Holiday not found"));

        return holidayMapper.toResponse(holiday);
    }

    @Override
    public HolidayResponse update(
            Long id,
            HolidayRequest request) {

        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Holiday not found"));

        holiday.setName(request.getName());
        holiday.setHolidayDate(request.getHolidayDate());
        holiday.setDescription(request.getDescription());

        return holidayMapper.toResponse(
                holidayRepository.save(holiday));
    }

    @Override
    public void delete(Long id) {

        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Holiday not found"));

        holiday.setIsActive(false);

        holidayRepository.save(holiday);
    }

    @Override
    public boolean isHoliday(LocalDate date) {

        return holidayRepository
                .existsByHolidayDateAndIsActiveTrue(date);
    }
}