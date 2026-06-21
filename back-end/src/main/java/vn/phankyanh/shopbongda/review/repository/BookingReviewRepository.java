package vn.phankyanh.shopbongda.review.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.phankyanh.shopbongda.review.domain.BookingReview;

@Repository
public interface BookingReviewRepository extends JpaRepository<BookingReview, Long> {
    boolean existsByBookingId(Long bookingId);

    List<BookingReview> findByBooking_Field_Id(Long fieldId);

    List<BookingReview> findByUser_Id(Long userId);

    @Query("""
        SELECT AVG(br.rating)
        FROM BookingReview br
        WHERE br.booking.field.id = :fieldId
    """)
    Double getAverageRating(Long fieldId);

    Long countByBooking_Field_Id(Long fieldId);

    Long countByBooking_Field_IdAndRating(Long fieldId, Integer rating);
}
