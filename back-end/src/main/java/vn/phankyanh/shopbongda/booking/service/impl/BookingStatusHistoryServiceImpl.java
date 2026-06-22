package vn.phankyanh.shopbongda.booking.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.domain.BookingStatusHistory;
import vn.phankyanh.shopbongda.booking.dto.response.BookingStatusHistoryResponse;
import vn.phankyanh.shopbongda.booking.repository.BookingStatusHistoryRepository;
import vn.phankyanh.shopbongda.booking.service.BookingStatusHistoryService;
import vn.phankyanh.shopbongda.user.domain.User;

@Service
@RequiredArgsConstructor
public class BookingStatusHistoryServiceImpl implements BookingStatusHistoryService {

    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;

    @Override
    @Transactional
    public void record(
            Booking booking,
            BookingStatus oldStatus,
            BookingStatus newStatus,
            User changedBy,
            String note
    ) {
        bookingStatusHistoryRepository.save(
                BookingStatusHistory.builder()
                        .booking(booking)
                        .oldStatus(oldStatus)
                        .newStatus(newStatus)
                        .changedBy(changedBy)
                        .changedByName(changedBy != null ? changedBy.getUsername() : "Hệ thống")
                        .changedByRole(changedBy != null && changedBy.getRole() != null ? changedBy.getRole().name() : "SYSTEM")
                        .note(note)
                        .build()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingStatusHistoryResponse> getByBookingId(Long bookingId) {
        return bookingStatusHistoryRepository
                .findByBookingIdOrderByCreatedAtAsc(bookingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BookingStatusHistoryResponse toResponse(BookingStatusHistory history) {
        return BookingStatusHistoryResponse.builder()
                .id(history.getId())
                .bookingId(history.getBooking().getId())
                .oldStatus(history.getOldStatus() != null ? history.getOldStatus().name() : null)
                .newStatus(history.getNewStatus().name())
                .changedById(history.getChangedBy() != null ? history.getChangedBy().getId() : null)
                .changedByName(history.getChangedByName())
                .changedByRole(history.getChangedByRole())
                .note(history.getNote())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
