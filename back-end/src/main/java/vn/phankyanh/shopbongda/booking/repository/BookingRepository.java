package vn.phankyanh.shopbongda.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.booking.domain.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Tìm kiếm các booking trùng lặp cho một sân bóng trong khoảng thời gian nhất định
    List<Booking> findByFieldIdAndStartTimeLessThanAndEndTimeGreaterThan(
        Long fieldId, LocalDateTime endTime, LocalDateTime startTime);
}
