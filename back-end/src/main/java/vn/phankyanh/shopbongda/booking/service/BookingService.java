package vn.phankyanh.shopbongda.booking.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.dto.request.BookingRequest;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.mapper.BookingMapper;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.field.domain.FootballField;
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

    public BookingResponse createBooking(BookingRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Invalid time range");
        }

        FootballField field = fieldRepository.findById(request.getFieldId())
            .orElseThrow(() -> new RuntimeException("Field not found"));

        List<Booking> conflicts = bookingRepository
            .findByFieldIdAndStartTimeLessThanAndEndTimeGreaterThan(
                field.getId(),
                request.getEndTime(),
                request.getStartTime()
            );

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Time slot already booked");
        }

        long minutes = Duration.between(
            request.getStartTime(),
            request.getEndTime()
        ).toMinutes();

        BigDecimal totalPrice = field.getPricePerHour()
            .multiply(BigDecimal.valueOf(minutes))
            .divide(BigDecimal.valueOf(60));

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
}
