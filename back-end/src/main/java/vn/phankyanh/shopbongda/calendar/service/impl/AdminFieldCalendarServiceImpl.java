package vn.phankyanh.shopbongda.calendar.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.calendar.dto.response.AdminFieldCalendarResponse;
import vn.phankyanh.shopbongda.calendar.service.AdminFieldCalendarService;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.maintenance.domain.Maintenance;
import vn.phankyanh.shopbongda.maintenance.repository.MaintenanceRepository;
import vn.phankyanh.shopbongda.pricing.service.PricingService;

@Service
@RequiredArgsConstructor
public class AdminFieldCalendarServiceImpl
        implements AdminFieldCalendarService {

    private static final LocalTime FIRST_SLOT = LocalTime.of(7, 0);
    private static final LocalTime LAST_SLOT_END = LocalTime.of(23, 45);
    private static final int SLOT_MINUTES = 90;

    private final FootballFieldRepository footballFieldRepository;
    private final BookingRepository bookingRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final PricingService pricingService;

    @Override
    @Transactional(readOnly = true)
    public AdminFieldCalendarResponse getFieldCalendar(LocalDate date) {

        LocalDate calendarDate =
                date != null ? date : LocalDate.now();

        LocalDateTime startOfDay =
                calendarDate.atStartOfDay();

        LocalDateTime endOfDay =
                calendarDate.plusDays(1).atStartOfDay();

        List<LocalTime> slotStarts =
                generateSlotStarts();

        List<FootballField> fields =
                footballFieldRepository.findAll()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        FootballField::getId
                                )
                        )
                        .toList();

        List<Booking> bookings =
                bookingRepository.findCalendarBookings(
                        startOfDay,
                        endOfDay
                );

        List<Maintenance> maintenances =
                maintenanceRepository.findCalendarMaintenances(
                        startOfDay,
                        endOfDay
                );

        List<AdminFieldCalendarResponse.FieldRow> fieldRows =
                fields.stream()
                        .map(field -> buildFieldRow(
                                calendarDate,
                                field,
                                slotStarts,
                                bookings,
                                maintenances
                        ))
                        .toList();

        return AdminFieldCalendarResponse.builder()
                .date(calendarDate)
                .slotStarts(slotStarts)
                .fields(fieldRows)
                .build();
    }

    private AdminFieldCalendarResponse.FieldRow buildFieldRow(
            LocalDate date,
            FootballField field,
            List<LocalTime> slotStarts,
            List<Booking> bookings,
            List<Maintenance> maintenances
    ) {

        List<AdminFieldCalendarResponse.SlotItem> slots =
                slotStarts.stream()
                        .map(slotStart ->
                                buildSlot(
                                        date,
                                        field,
                                        slotStart,
                                        bookings,
                                        maintenances
                                )
                        )
                        .toList();

        return AdminFieldCalendarResponse.FieldRow.builder()
                .fieldId(field.getId())
                .fieldName(field.getName())
                .fieldType(field.getType().name())
                .fieldStatus(field.getStatus().name())
                .slots(slots)
                .build();
    }

    private AdminFieldCalendarResponse.SlotItem buildSlot(
            LocalDate date,
            FootballField field,
            LocalTime slotStart,
            List<Booking> bookings,
            List<Maintenance> maintenances
    ) {

        LocalTime slotEnd =
                getSlotEnd(slotStart);

        LocalDateTime slotStartDateTime =
                LocalDateTime.of(date, slotStart);

        LocalDateTime slotEndDateTime =
                LocalDateTime.of(date, slotEnd);

        Maintenance maintenance =
                findOverlappingMaintenance(
                        field.getId(),
                        slotStartDateTime,
                        slotEndDateTime,
                        maintenances
                );

        if (maintenance != null) {
            return AdminFieldCalendarResponse.SlotItem.builder()
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .type("MAINTENANCE")
                    .status("MAINTENANCE")
                    .maintenanceId(maintenance.getId())
                    .fieldName(field.getName())
                    .reason(maintenance.getReason())
                    .price(calculatePrice(field.getId(), slotStartDateTime))
                    .build();
        }

        Booking booking =
                findOverlappingBooking(
                        field.getId(),
                        slotStartDateTime,
                        slotEndDateTime,
                        bookings
                );

        if (booking != null) {
            return AdminFieldCalendarResponse.SlotItem.builder()
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .type("BOOKING")
                    .status(booking.getStatus().name())
                    .bookingId(booking.getId())
                    .customerName(booking.getUser().getUsername())
                    .fieldName(field.getName())
                    .price(booking.getTotalPrice())
                    .build();
        }

        return AdminFieldCalendarResponse.SlotItem.builder()
                .startTime(slotStart)
                .endTime(slotEnd)
                .type("AVAILABLE")
                .status("AVAILABLE")
                .fieldName(field.getName())
                .price(calculatePrice(field.getId(), slotStartDateTime))
                .build();
    }

    private Maintenance findOverlappingMaintenance(
            Long fieldId,
            LocalDateTime slotStart,
            LocalDateTime slotEnd,
            List<Maintenance> maintenances
    ) {

        return maintenances.stream()
                .filter(maintenance ->
                        maintenance.getFieldId().equals(fieldId)
                                && maintenance.getStartTime().isBefore(slotEnd)
                                && maintenance.getEndTime().isAfter(slotStart)
                )
                .findFirst()
                .orElse(null);
    }

    private Booking findOverlappingBooking(
            Long fieldId,
            LocalDateTime slotStart,
            LocalDateTime slotEnd,
            List<Booking> bookings
    ) {

        return bookings.stream()
                .filter(booking ->
                        booking.getField().getId().equals(fieldId)
                                && booking.getStartTime().isBefore(slotEnd)
                                && booking.getEndTime().isAfter(slotStart)
                )
                .findFirst()
                .orElse(null);
    }

    private BigDecimal calculatePrice(
            Long fieldId,
            LocalDateTime slotStartDateTime
    ) {

        try {
            return pricingService.calculateSlotPrice(
                    fieldId,
                    slotStartDateTime
            );
        } catch (RuntimeException exception) {
            return BigDecimal.ZERO;
        }
    }

    private List<LocalTime> generateSlotStarts() {

        List<LocalTime> slots =
                new ArrayList<>();

        int firstSlotMinute =
                FIRST_SLOT.toSecondOfDay() / 60;

        int lastSlotEndMinute =
                LAST_SLOT_END.toSecondOfDay() / 60;

        for (
                int currentMinute = firstSlotMinute;
                currentMinute < lastSlotEndMinute;
                currentMinute += SLOT_MINUTES
        ) {
            slots.add(
                    LocalTime.of(
                            currentMinute / 60,
                            currentMinute % 60
                    )
            );
        }

        return slots;
    }

    private LocalTime getSlotEnd(LocalTime slotStart) {

        int slotStartMinute =
                slotStart.toSecondOfDay() / 60;

        int slotEndMinute =
                slotStartMinute + SLOT_MINUTES;

        int lastSlotEndMinute =
                LAST_SLOT_END.toSecondOfDay() / 60;

        if (slotEndMinute >= lastSlotEndMinute) {
            return LAST_SLOT_END;
        }

        return LocalTime.of(
                slotEndMinute / 60,
                slotEndMinute % 60
        );
    }
}
