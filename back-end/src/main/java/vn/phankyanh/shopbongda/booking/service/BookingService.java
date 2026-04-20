package vn.phankyanh.shopbongda.booking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.dto.request.BookingRequest;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.mapper.BookingMapper;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.dto.response.SlotResponse;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final FootballFieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long userId) {

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Time is required");
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Invalid time range");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book past time");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        FootballField field = fieldRepository.findById(request.getFieldId())
            .orElseThrow(() -> new RuntimeException("Field not found"));

        if (field.getStatus() != FieldStatus.AVAILABLE) {
            throw new RuntimeException("Field is not available");
        }

        List<Booking> conflicts = bookingRepository
            .findByFieldIdAndStartTimeLessThanAndEndTimeGreaterThan(
                field.getId(),
                request.getEndTime(),
                request.getStartTime()
            );

        boolean hasConflict = conflicts.stream().anyMatch(b ->
            b.getStatus() != BookingStatus.CANCELLED
        );

        if (hasConflict) {
            throw new IllegalStateException("Time slot already booked");
        }

        long minutes = Duration.between(
            request.getStartTime(),
            request.getEndTime()
        ).toMinutes();

        BigDecimal totalPrice = calculatePrice(field, minutes);

        Booking booking = Booking.builder()
            .user(user)
            .field(field)
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .totalPrice(totalPrice)
            .status(BookingStatus.PENDING)
            .build();

        bookingRepository.save(booking);

        return bookingMapper.toResponse(booking);
    }

    public Page<BookingResponse> getAllBooking(
        String keyword,
        BookingStatus status,
        Pageable pageable
) {
    return bookingRepository
        .searchBookings(keyword, status, pageable)
        .map(bookingMapper::toResponse);
}

    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
            .map(bookingMapper::toResponse)
            .toList();
    }

    private static final int SLOT_DURATION = 90;
    private static final LocalTime OPEN_TIME = LocalTime.of(6, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(22, 0);

    public List<SlotResponse> getAvailableSlots(Long fieldId, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Booking> bookings = bookingRepository
                .findBookingsInDay(fieldId, startOfDay, endOfDay);

        List<SlotResponse> result = new ArrayList<>();

        LocalTime current = OPEN_TIME;

        while (current.plusMinutes(SLOT_DURATION).isBefore(CLOSE_TIME) 
            || current.plusMinutes(SLOT_DURATION).equals(CLOSE_TIME)) {

            LocalDateTime slotStart = LocalDateTime.of(date, current);
            LocalDateTime slotEnd = slotStart.plusMinutes(SLOT_DURATION);

            boolean isBooked = bookings.stream().anyMatch(b ->
                    b.getStatus() != BookingStatus.CANCELLED &&
                    slotStart.isBefore(b.getEndTime()) &&
                    slotEnd.isAfter(b.getStartTime())
            );

            result.add(
                SlotResponse.builder()
                    .startTime(current)
                    .endTime(current.plusMinutes(SLOT_DURATION))
                    .available(!isBooked)
                    .build()
            );

            current = current.plusMinutes(SLOT_DURATION);
        }

        return result;
    }

    public void confirmBooking(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if(booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        if(booking.getStartTime().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Cannot cancel past bookings");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);
    } 

    private BigDecimal calculatePrice(FootballField field, long minutes) {
        return field.getPricePerHour()
            .multiply(BigDecimal.valueOf(minutes))
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
