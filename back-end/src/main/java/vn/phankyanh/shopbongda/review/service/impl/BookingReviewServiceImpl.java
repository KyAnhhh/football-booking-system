package vn.phankyanh.shopbongda.review.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.review.domain.BookingReview;
import vn.phankyanh.shopbongda.review.dto.request.BookingReviewRequest;
import vn.phankyanh.shopbongda.review.dto.response.BookingReviewResponse;
import vn.phankyanh.shopbongda.review.dto.response.BookingReviewSummaryResponse;
import vn.phankyanh.shopbongda.review.dto.response.AdminReviewResponse;
import vn.phankyanh.shopbongda.review.mapper.BookingReviewMapper;
import vn.phankyanh.shopbongda.review.repository.BookingReviewRepository;
import vn.phankyanh.shopbongda.review.service.BookingReviewService;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BookingReviewServiceImpl implements BookingReviewService {
    private final BookingReviewRepository bookingReviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingReviewMapper bookingReviewMapper;

    @Override
    @Transactional
    public BookingReviewResponse createReview(
            Long bookingId,
            BookingReviewRequest request,
            Long userId
    ) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // check owner booking
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only review your own bookings");
        }

        // check booking completed
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Only completed bookings can be reviewed");
        }

        // check already reviewed
        if (bookingReviewRepository.existsByBookingId(bookingId)) {
            throw new RuntimeException("Review for this booking already exists");
        }

        BookingReview bookingReview =
                bookingReviewMapper.toReview(request);

        bookingReview.setBooking(booking);
        bookingReview.setUser(user);
        bookingReview.setCreatedAt(LocalDateTime.now());

        BookingReview savedReview =
                bookingReviewRepository.save(bookingReview);

        return bookingReviewMapper.toResponse(savedReview);
    }

    @Override
    public List<BookingReviewResponse> getFieldReviews(Long fieldId) {

        return bookingReviewRepository
                .findByBooking_Field_Id(fieldId)
                .stream()
                .map(bookingReviewMapper::toResponse)
                .toList();
    }

    @Override
    public BookingReviewSummaryResponse getFieldReviewSummary(Long fieldId) {
        Long totalReviews = bookingReviewRepository.countByBooking_Field_Id(fieldId);
        Double averageRating = totalReviews == 0 ? 0.0 : bookingReviewRepository.getAverageRating(fieldId);

        return BookingReviewSummaryResponse.builder()
                .fieldId(fieldId)
                .averageRating(averageRating == null ? 0.0 : averageRating)
                .totalReviews(totalReviews)
                .fiveStarCount(countRating(fieldId, 5))
                .fourStarCount(countRating(fieldId, 4))
                .threeStarCount(countRating(fieldId, 3))
                .twoStarCount(countRating(fieldId, 2))
                .oneStarCount(countRating(fieldId, 1))
                .build();
    }

    private Long countRating(Long fieldId, Integer rating) {
        return bookingReviewRepository.countByBooking_Field_IdAndRating(fieldId, rating);
    }

    @Override
    public List<BookingReviewResponse> getMyReviews(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingReviewRepository
                .findByUser_Id(user.getId())
                .stream()
                .map(bookingReviewMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<AdminReviewResponse> getAdminReviews() {
        return bookingReviewRepository.findAll().stream()
                .map(review -> AdminReviewResponse.builder()
                        .id(review.getId())
                        .reviewType("BOOKING")
                        .sourceId(review.getBooking().getId())
                        .subjectName(review.getBooking().getField().getName())
                        .userName(review.getUser().getUsername())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        BookingReview review = bookingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Booking review not found"));
        bookingReviewRepository.delete(review);
    }
}
