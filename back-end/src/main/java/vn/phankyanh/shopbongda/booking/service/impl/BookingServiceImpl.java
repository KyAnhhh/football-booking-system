package vn.phankyanh.shopbongda.booking.service.impl;

import java.math.BigDecimal;
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
import vn.phankyanh.shopbongda.extra.domain.BookingExtraService;
import vn.phankyanh.shopbongda.extra.domain.ExtraService;
import vn.phankyanh.shopbongda.extra.dto.request.BookingExtraServiceRequest;
import vn.phankyanh.shopbongda.extra.repository.ExtraServiceRepository;
import vn.phankyanh.shopbongda.extra.repository.BookingExtraServiceRepository;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.dto.response.SlotResponse;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.maintenance.service.MaintenanceService;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;
import vn.phankyanh.shopbongda.payment.domain.PaymentType;
import vn.phankyanh.shopbongda.shared.service.email.EmailService;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;
import vn.phankyanh.shopbongda.pricing.service.PricingService;
import vn.phankyanh.shopbongda.membership.service.MembershipService;
import vn.phankyanh.shopbongda.voucher.service.VoucherApplicationResult;
import vn.phankyanh.shopbongda.voucher.service.VoucherService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FootballFieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final EmailService emailService;
    private final MaintenanceService maintenanceService;
    private final PricingService pricingService;
    private final MembershipService membershipService;
    private final VoucherService voucherService;
    private final ExtraServiceRepository extraServiceRepository;
    private final BookingExtraServiceRepository bookingExtraServiceRepository;
    

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

                if (request.getFieldId() == null) {

                throw new IllegalArgumentException(
                        "Field is required"
                );
                }

                if (!request.getEndTime()
                        .isAfter(request.getStartTime())) {

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

                if (field.getStatus() != vn.phankyanh.shopbongda.field.domain.FieldStatus.AVAILABLE) {

                throw new RuntimeException(
                        "Field is not available"
                );
                }

                if (!maintenanceService.isFieldAvailable(field.getId(), request.getStartTime(), request.getEndTime())) {
                throw new RuntimeException("Field is under maintenance during this time");
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

                BigDecimal fieldPrice =
                        pricingService.calculatePrice(
                                field.getId(),
                                request.getStartTime(),
                                request.getEndTime()
                        );

                BigDecimal extraServicePrice =
                        calculateExtraServicePrice(
                                request.getServices()
                        );

                BigDecimal originalPrice =
                        fieldPrice.add(extraServicePrice);

                BigDecimal priceAfterMembership =
                        membershipService.applyMembershipDiscount(
                                user.getId(),
                                originalPrice
                        );

                BigDecimal membershipDiscount =
                        calculateDiscountAmount(
                                originalPrice,
                                priceAfterMembership
                        );

                String voucherCode =
                        normalizeVoucherCode(
                                request.getVoucherCode()
                        );

                VoucherApplicationResult voucherApplication =
                        null;

                BigDecimal priceAfterVoucher =
                        priceAfterMembership;

                if (voucherCode != null) {
                        voucherApplication =
                                voucherService.consumeVoucher(
                                        voucherCode,
                                        priceAfterMembership
                                );

                        priceAfterVoucher =
                                voucherApplication.getFinalAmount();
                }

                BigDecimal voucherDiscount =
                        calculateDiscountAmount(
                                priceAfterMembership,
                                priceAfterVoucher
                        );

                BigDecimal totalPrice =
                        priceAfterVoucher;

                Booking booking = Booking.builder()
                        .user(user)
                        .field(field)
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .contactPhone(request.getContactPhone())
                        .note(request.getNote())
                        .originalPrice(originalPrice)
                        .extraServicePrice(extraServicePrice)
                        .membershipDiscount(membershipDiscount)
                        .voucherDiscount(voucherDiscount)
                        .voucherCode(voucherCode)
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

                Booking savedBooking =
                        bookingRepository.save(booking);

                voucherService.recordUsage(
                        voucherApplication,
                        user.getId(),
                        savedBooking.getId()
                );

                List<BookingExtraService> bookingExtraServices =
                        saveBookingExtraServices(
                        savedBooking,
                        request.getServices()
                );

                savedBooking.setExtraServices(
                        bookingExtraServices
                );

                return bookingMapper.toResponse(savedBooking);
                }

        private BigDecimal calculateDiscountAmount(
                BigDecimal priceBeforeDiscount,
                BigDecimal priceAfterDiscount
        ) {

                BigDecimal discount =
                        priceBeforeDiscount.subtract(
                                priceAfterDiscount
                        );

                if (discount.compareTo(BigDecimal.ZERO) < 0) {
                        return BigDecimal.ZERO;
                }

                return discount;
        }

        private String normalizeVoucherCode(
                String voucherCode
        ) {

                if (voucherCode == null || voucherCode.isBlank()) {
                        return null;
                }

                return voucherCode.trim();
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

                boolean isUnderMaintenance = !maintenanceService.isFieldAvailable(fieldId, slotStart, slotEnd);

                result.add(
                        SlotResponse.builder()
                                .startTime(slotStart.toLocalTime())
                                .endTime(slotEnd.toLocalTime())
                                .available(!isBooked && !isPastSlot && !isUnderMaintenance)
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
                        bookingRepository.findByIdForUpdate(bookingId)
                                .orElseThrow(() ->
                                        new RuntimeException("Booking not found"));

                confirmPendingBooking(booking);

                bookingRepository.save(booking);
        }

        @Transactional
        public BookingResponse updateBookingStatus(
                Long bookingId,
                UpdateBookingStatusRequest request
        ) {

        Booking booking = bookingRepository
                .findByIdForUpdate(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        BookingStatus currentStatus = booking.getStatus();
        BookingStatus newStatus = request.getBookingStatus();

        // check null
        if (newStatus == null) {
                throw new RuntimeException("Booking status is required");
        }

        // tránh update cùng status
        if (currentStatus == newStatus) {
                throw new RuntimeException(
                        "Booking is already in this status"
                );
        }
        
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

        if (newStatus == BookingStatus.CONFIRMED) {
                confirmPendingBooking(booking);
        } else {
                booking.setStatus(newStatus);
        }

        Booking updatedBooking =
                bookingRepository.save(booking);

        if(newStatus == BookingStatus.CONFIRMED
                || newStatus == BookingStatus.CANCELLED
                || newStatus == BookingStatus.COMPLETED
        ) {
                emailService.sendEmailWhenBookingStatusChange(
                        updatedBooking.getUser().getEmail(),
                        updatedBooking.getId(),
                        updatedBooking.getStatus()
                );
        }

        return bookingMapper.toResponse(updatedBooking);
        }

        private void confirmPendingBooking(
                Booking booking
        ) {

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

                membershipService.addPoints(
                        booking.getUser().getId(),
                        booking.getTotalPrice()
                );

                booking.setStatus(
                        BookingStatus.CONFIRMED
                );
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


        private BigDecimal calculateExtraServicePrice(List<BookingExtraServiceRequest> services) {

                if (services == null || services.isEmpty()) {
                        return BigDecimal.ZERO;
                }

                BigDecimal total = BigDecimal.ZERO;

        for (BookingExtraServiceRequest item : services) {

                validateExtraServiceRequest(item);

                ExtraService service =
                        extraServiceRepository
                                .findById(item.getServiceId())
                                        .orElseThrow(() ->
                                                new RuntimeException(
                                                        "Extra service not found"));

                        if (!Boolean.TRUE.equals(
                                service.getIsActive())) {

                        throw new RuntimeException(
                                "Extra service inactive");
                        }

                        BigDecimal itemPrice =
                                service.getPrice()
                                        .multiply(
                                                BigDecimal.valueOf(
                                                        item.getQuantity()));

                        total = total.add(itemPrice);
                }

                return total;
        }

        private List<BookingExtraService> saveBookingExtraServices(
                Booking booking,
                List<BookingExtraServiceRequest> services
        ) {

        if (services == null || services.isEmpty()) {
                return List.of();
        }

        List<BookingExtraService> savedServices =
                new ArrayList<>();

        for (BookingExtraServiceRequest item : services) {

                validateExtraServiceRequest(item);

                ExtraService service =
                        extraServiceRepository.findById(
                                item.getServiceId()
                        ).orElseThrow(
                                () -> new RuntimeException(
                                        "Extra service not found"
                                )
                        );

                BookingExtraService bookingService =
                        BookingExtraService.builder()
                                .booking(booking)
                                .extraService(service)
                                .quantity(item.getQuantity())
                                .unitPrice(service.getPrice())
                                .totalPrice(
                                        service.getPrice().multiply(
                                                BigDecimal.valueOf(
                                                        item.getQuantity()
                                                )
                                        )
                                )
                                .build();

                BookingExtraService savedService =
                        bookingExtraServiceRepository.save(
                        bookingService
                );

                savedServices.add(savedService);
        }

        return savedServices;
        }

        private void validateExtraServiceRequest(
                BookingExtraServiceRequest item
        ) {

        if (item == null) {
                throw new IllegalArgumentException(
                        "Extra service is required"
                );
        }

        if (item.getServiceId() == null) {
                throw new IllegalArgumentException(
                        "Extra service id is required"
                );
        }

        if (item.getQuantity() == null
                || item.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Extra service quantity must be greater than 0"
                );
        }
        }
}
