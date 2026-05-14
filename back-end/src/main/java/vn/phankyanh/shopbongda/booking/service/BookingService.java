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
import vn.phankyanh.shopbongda.booking.projection.BookingTimeView;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.dto.response.SlotResponse;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;
import vn.phankyanh.shopbongda.payment.domain.PaymentType;
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

        // FootballField field = fieldRepository.findById(request.getFieldId())
        //     .orElseThrow(() -> new RuntimeException("Field not found"));
        FootballField field = fieldRepository.getReferenceById(request.getFieldId());

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

        Payment payment = Payment.builder()
            .paymentType(PaymentType.BOOKING)
            .paymentStatus(PaymentStatus.UNPAID)
            .totalAmount(totalPrice)
            .paymentMethod(request.getPaymentMethod())
            .build();

        booking.setPayment(payment);
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
    private static final LocalTime OPEN_TIME = LocalTime.of(7, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(23, 30);

    public List<SlotResponse> getAvailableSlots(Long fieldId, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<BookingTimeView> bookings =
                bookingRepository.findBookingsInDayLite(fieldId, startOfDay, endOfDay);

        System.out.println("Bookings size = " + bookings.size());
        
        List<SlotResponse> result = new ArrayList<>();

        LocalTime current = OPEN_TIME;

        while (current.plusMinutes(SLOT_DURATION).compareTo(CLOSE_TIME) <= 0) {

            LocalDateTime slotStart = LocalDateTime.of(date, current);
            LocalDateTime slotEnd = slotStart.plusMinutes(SLOT_DURATION);

            boolean isBooked = false;

            for (BookingTimeView b : bookings) {
                if (b.getStatus() != BookingStatus.CANCELLED &&
                    slotStart.isBefore(b.getEndTime()) &&
                    slotEnd.isAfter(b.getStartTime())) {
                    isBooked = true;
                    break;
                }
            }

            result.add(SlotResponse.builder()
                    .startTime(current)
                    .endTime(current.plusMinutes(SLOT_DURATION))
                    .available(!isBooked)
                    .build());

            current = current.plusMinutes(SLOT_DURATION);
        }

        return result;
    }

    // public List<SlotResponse> getAvailableSlots(Long fieldId, LocalDate date) {

    //     return List.of(
    //         SlotResponse.builder()
    //             .startTime(LocalTime.of(7, 0))
    //             .endTime(LocalTime.of(8, 30))
    //             .available(true)
    //             .build()
    //     );
    // }

    public BookingResponse getBookingForQr(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking not payable");
        }

        if (booking.getPayment().getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Booking already paid");
        }

        return bookingMapper.toResponse(booking);
    }

    public void confirmBooking(Long bookingId){

        Booking booking = bookingRepository.getReferenceById(bookingId);

        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }

        Payment payment = booking.getPayment();

        if (payment != null &&
            payment.getPaymentMethod() != PaymentMethod.COD &&
            payment.getPaymentStatus() != PaymentStatus.PAID) {

            throw new IllegalStateException("Booking not paid");
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.getReferenceById(bookingId);

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if(booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        if(booking.getStartTime().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Cannot cancel past bookings");
        }

        Payment payment = booking.getPayment();

        if(payment != null &&
        payment.getPaymentStatus() == PaymentStatus.UNPAID) {

            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setPaymentDate(LocalDateTime.now());
        }

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);
    } 

    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        bookingRepository.delete(booking);
    }

    private BigDecimal calculatePrice(FootballField field, long minutes) {
        return field.getPricePerHour()
            .multiply(BigDecimal.valueOf(minutes))
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
