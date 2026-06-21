package vn.phankyanh.shopbongda.notification.service;

import java.util.List;

import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.notification.dto.response.NotificationResponse;

public interface NotificationService {

    void notifyAdminsBookingCreated(Booking booking);

    void notifyUserBookingStatusChanged(Booking booking, BookingStatus previousStatus);

    List<NotificationResponse> getMyNotifications(Long userId, int limit);

    long countUnread(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);
}
