package vn.phankyanh.shopbongda.booking.service.impl;

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
import vn.phankyanh.shopbongda.booking.dto.request.UpdateBookingStatusRequest;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.mapper.BookingMapper;
import vn.phankyanh.shopbongda.booking.projection.BookingTimeView;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.booking.service.BookingService;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.dto.response.SlotResponse;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;
import vn.phankyanh.shopbongda.payment.domain.PaymentType;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FootballFieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    private static final int SLOT_DURATION = 90;

    private static final LocalTime OPEN_TIME =
            LocalTime.of(7, 0);

    private static final LocalTime CLOSE_TIME =
            LocalTime.of(23, 45);

    @Override
    @Transactional
    public BookingResponse createBooking(
            BookingRequest request,
            Long userId
    ) {

        if (request.getStartTime() == null ||
                request.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Time is required"
            );
        }

        if (request.getStartTime()
                .isAfter(request.getEndTime())) {

            throw new IllegalArgumentException(
                    "Invalid time range"
            );
        }

        if (request.getStartTime()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Cannot book past time"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        FootballField field =
                fieldRepository.findById(request.getFieldId())
                        .orElseThrow(() ->
                                new RuntimeException("Field not found"));

        if (field.getStatus() != FieldStatus.AVAILABLE) {

            throw new RuntimeException(
                    "Field is not available"
            );
        }

        List<Booking> conflicts =
                bookingRepository.findConflictsForUpdate(
                        field.getId(),
                        request.getStartTime(),
                        request.getEndTime()
                );

        if (!conflicts.isEmpty()) {

            throw new IllegalStateException(
                    "Time slot already booked"
            );
        }

        long minutes = Duration.between(
                request.getStartTime(),
                request.getEndTime()
        ).toMinutes();

        BigDecimal totalPrice =
                calculatePrice(field, minutes);

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

        @Override
        public Page<BookingResponse> getAllBooking(
                String keyword,
                BookingStatus status,
                Pageable pageable
        ) {

                return bookingRepository
                        .searchBookings(keyword, status, pageable)
                        .map(bookingMapper::toResponse);
        }

        @Override
        public List<BookingResponse> getBookingsByUserId(
                Long userId
        ) {

                return bookingRepository.findByUserId(userId)
                        .stream()
                        .map(bookingMapper::toResponse)
                        .toList();
        }

        @Override
        public List<SlotResponse> getAvailableSlots(
                Long fieldId,
                LocalDate date
        ) {

                LocalDateTime base =
                        date.atTime(OPEN_TIME);

                LocalDateTime close =
                        date.atTime(CLOSE_TIME);

                List<BookingTimeView> bookings =
                        bookingRepository.findBookingsInDayLite(
                                fieldId,
                                base,
                                close
                        );

                List<SlotResponse> result =
                        new ArrayList<>();

                LocalDateTime now =
                        LocalDateTime.now();

                int index = 0;

                while (true) {

                LocalDateTime slotStart =
                        base.plusMinutes(
                                (long) index * SLOT_DURATION
                        );

                LocalDateTime slotEnd =
                        slotStart.plusMinutes(SLOT_DURATION);

                if (slotEnd.isAfter(close)) {
                        break;
                }

                boolean isBooked =
                        bookings.stream().anyMatch(b ->
                                b.getStatus() != BookingStatus.CANCELLED &&
                                slotStart.isBefore(b.getEndTime()) &&
                                slotEnd.isAfter(b.getStartTime())
                        );

                boolean isPastSlot =
                        !slotStart.isAfter(now);

                result.add(
                        SlotResponse.builder()
                                .startTime(slotStart.toLocalTime())
                                .endTime(slotEnd.toLocalTime())
                                .available(!isBooked && !isPastSlot)
                                .build()
                );

                index++;
                }

                return result;
        }

        @Override
        public BookingResponse getBookingForQr(
                Long bookingId
        ) {

                Booking booking =
                        bookingRepository.findById(bookingId)
                                .orElseThrow(() ->
                                        new RuntimeException("Booking not found"));

                if (booking.getStatus() != BookingStatus.PENDING) {

                throw new RuntimeException(
                        "Booking not payable"
                );
                }

                if (booking.getPayment().getPaymentStatus()
                        == PaymentStatus.PAID) {

                throw new RuntimeException(
                        "Booking already paid"
                );
                }

                return bookingMapper.toResponse(booking);
        }

        @Override
        @Transactional
        public void confirmBooking(Long bookingId) {

                Booking booking =
                        bookingRepository.findById(bookingId)
                                .orElseThrow(() ->
                                        new RuntimeException("Booking not found"));

                if (booking.getStatus() != BookingStatus.PENDING) {

                throw new IllegalStateException(
                        "Booking already processed"
                );
                }

                Payment payment =
                        booking.getPayment();

                if (payment == null) {

                throw new RuntimeException(
                        "Payment not found"
                );
                }

                payment.setPaymentStatus(
                        PaymentStatus.PAID
                );

                payment.setPaymentDate(
                        LocalDateTime.now()
                );

                booking.setStatus(
                        BookingStatus.CONFIRMED
                );

                bookingRepository.save(booking);
        }

        @Transactional
        public BookingResponse updateBookingStatus(
                Long bookingId,
                UpdateBookingStatusRequest request
        ) {

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        BookingStatus currentStatus = booking.getStatus();
        BookingStatus newStatus = request.getBookingStatus();

        // không cho update nếu đã completed
        if (currentStatus == BookingStatus.COMPLETED) {
                throw new RuntimeException(
                        "Completed booking cannot be updated"
                );
        }

        // không cho update nếu đã cancelled
        if (currentStatus == BookingStatus.CANCELLED) {
                throw new RuntimeException(
                        "Cancelled booking cannot be updated"
                );
        }

        // validate flow
        switch (currentStatus) {

                case PENDING -> {

                // chỉ cho:
                // PENDING -> CONFIRMED
                // PENDING -> CANCELLED
                if (newStatus != BookingStatus.CONFIRMED
                        && newStatus != BookingStatus.CANCELLED) {

                        throw new RuntimeException(
                                "Invalid status transition from PENDING"
                        );
                }
                }

                case CONFIRMED -> {

                // chỉ cho:
                // CONFIRMED -> COMPLETED
                // CONFIRMED -> CANCELLED
                if (newStatus != BookingStatus.COMPLETED
                        && newStatus != BookingStatus.CANCELLED) {

                        throw new RuntimeException(
                                "Invalid status transition from CONFIRMED"
                        );
                }
                }

                default -> throw new RuntimeException(
                        "Invalid booking status"
                );
        }

        booking.setStatus(newStatus);

        Booking updatedBooking =
                bookingRepository.save(booking);

        return bookingMapper.toResponse(updatedBooking);
        }

        @Override
        public void cancelBooking(
                Long bookingId,
                Long userId
        ) {

                Booking booking =
                        bookingRepository.getReferenceById(bookingId);

                if (!booking.getUser().getId().equals(userId)) {

                throw new RuntimeException(
                        "Unauthorized"
                );
                }

                if (booking.getStatus() ==
                        BookingStatus.CANCELLED) {

                throw new IllegalStateException(
                        "Booking is already cancelled"
                );
                }

                if (booking.getStartTime()
                        .isBefore(LocalDateTime.now())) {

                throw new IllegalStateException(
                        "Cannot cancel past bookings"
                );
                }

                Payment payment =
                        booking.getPayment();

                if (payment != null &&
                        payment.getPaymentStatus()
                                == PaymentStatus.UNPAID) {

                payment.setPaymentStatus(
                        PaymentStatus.FAILED
                );

                payment.setPaymentDate(
                        LocalDateTime.now()
                );
                }

                booking.setStatus(
                        BookingStatus.CANCELLED
                );

                bookingRepository.save(booking);
        }

        @Override
        public void deleteBooking(Long bookingId) {

                Booking booking =
                        bookingRepository.findById(bookingId)
                                .orElseThrow(() ->
                                        new RuntimeException("Booking not found"));

                bookingRepository.delete(booking);
        }

        private BigDecimal calculatePrice(
                FootballField field,
                long minutes
        ) {

                return field.getPricePerHour()
                        .multiply(BigDecimal.valueOf(minutes))
                        .divide(
                                BigDecimal.valueOf(60),
                                2,
                                RoundingMode.HALF_UP
                        );
        }
}