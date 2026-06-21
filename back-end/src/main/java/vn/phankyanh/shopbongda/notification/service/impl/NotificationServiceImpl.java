package vn.phankyanh.shopbongda.notification.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.notification.domain.Notification;
import vn.phankyanh.shopbongda.notification.domain.NotificationType;
import vn.phankyanh.shopbongda.notification.dto.response.NotificationResponse;
import vn.phankyanh.shopbongda.notification.repository.NotificationRepository;
import vn.phankyanh.shopbongda.notification.service.NotificationService;
import vn.phankyanh.shopbongda.user.domain.Role;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String BOOKING_REFERENCE_TYPE = "BOOKING";
    private static final DateTimeFormatter BOOKING_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void notifyAdminsBookingCreated(Booking booking) {
        List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);

        for (User admin : admins) {
            notificationRepository.save(
                    Notification.builder()
                            .user(admin)
                            .type(NotificationType.BOOKING_CREATED)
                            .title("Có lịch đặt sân mới")
                            .message(buildBookingCreatedMessage(booking))
                            .referenceType(BOOKING_REFERENCE_TYPE)
                            .referenceId(booking.getId())
                            .isRead(false)
                            .build()
            );
        }
    }

    @Override
    @Transactional
    public void notifyUserBookingStatusChanged(
            Booking booking,
            BookingStatus previousStatus
    ) {
        notificationRepository.save(
                Notification.builder()
                        .user(booking.getUser())
                        .type(NotificationType.BOOKING_STATUS_CHANGED)
                        .title("Lịch đặt sân đã cập nhật")
                        .message(buildStatusChangedMessage(booking, previousStatus))
                        .referenceType(BOOKING_REFERENCE_TYPE)
                        .referenceId(booking.getId())
                        .isRead(false)
                        .build()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Long userId, int limit) {
        int normalizedLimit = Math.max(1, Math.min(limit, 50));

        return notificationRepository
                .findLatestByUserId(userId, PageRequest.of(0, normalizedLimit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Notification not found");
        }

        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }

    private String buildBookingCreatedMessage(Booking booking) {
        return String.format(
                "%s vừa đặt %s lúc %s.",
                booking.getUser().getUsername(),
                booking.getField().getName(),
                booking.getStartTime().format(BOOKING_TIME_FORMAT)
        );
    }

    private String buildStatusChangedMessage(
            Booking booking,
            BookingStatus previousStatus
    ) {
        return String.format(
                "Lịch đặt sân #%d đã chuyển từ %s sang %s.",
                booking.getId(),
                getStatusLabel(previousStatus),
                getStatusLabel(booking.getStatus())
        );
    }

    private String getStatusLabel(BookingStatus status) {
        return switch (status) {
            case PENDING -> "Chờ xác nhận";
            case CONFIRMED -> "Đã xác nhận";
            case CANCELLED -> "Đã hủy";
            case IN_PROGRESS -> "Đang diễn ra";
            case COMPLETED -> "Hoàn tất";
            case EXPIRED -> "Hết hạn";
        };
    }
}
