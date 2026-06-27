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
import vn.phankyanh.shopbongda.order.domain.Order;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.user.domain.Role;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String BOOKING_REFERENCE_TYPE = "BOOKING";
    private static final String ORDER_REFERENCE_TYPE = "ORDER";
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
    @Transactional
    public void notifyOrderCreated(Order order) {
        notificationRepository.save(
                buildOrderNotification(
                        order.getUser(),
                        NotificationType.ORDER_CREATED,
                        "Đặt hàng thành công",
                        String.format(
                                "Đơn hàng #%d đã được tạo và đang chờ xử lý.",
                                order.getId()
                        ),
                        order.getId()
                )
        );

        for (User operator : getOrderOperators()) {
            notificationRepository.save(
                    buildOrderNotification(
                            operator,
                            NotificationType.ORDER_CREATED,
                            "Có đơn hàng mới",
                            String.format(
                                    "%s vừa tạo đơn hàng #%d trị giá %,.0f đ.",
                                    order.getUser().getUsername(),
                                    order.getId(),
                                    order.getTotalAmount()
                            ),
                            order.getId()
                    )
            );
        }
    }

    @Override
    @Transactional
    public void notifyOrderPaymentSubmitted(Order order) {
        for (User operator : getOrderOperators()) {
            notificationRepository.save(
                    buildOrderNotification(
                            operator,
                            NotificationType.ORDER_PAYMENT_SUBMITTED,
                            "Khách đã báo chuyển khoản",
                            String.format(
                                    "Đơn hàng #%d đang chờ kiểm tra giao dịch VietQR.",
                                    order.getId()
                            ),
                            order.getId()
                    )
            );
        }
    }

    @Override
    @Transactional
    public void notifyUserOrderStatusChanged(
            Order order,
            OrderStatus previousStatus
    ) {
        notificationRepository.save(
                buildOrderNotification(
                        order.getUser(),
                        NotificationType.ORDER_STATUS_CHANGED,
                        "Đơn hàng đã cập nhật",
                        String.format(
                                "Đơn hàng #%d đã chuyển từ %s sang %s.",
                                order.getId(),
                                getOrderStatusLabel(previousStatus),
                                getOrderStatusLabel(order.getOrderStatus())
                        ),
                        order.getId()
                )
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

    private List<User> getOrderOperators() {
        return List.of(
                Role.ROLE_ADMIN,
                Role.ROLE_MANAGER,
                Role.ROLE_STAFF
        ).stream()
                .flatMap(role -> userRepository.findByRole(role).stream())
                .toList();
    }

    private Notification buildOrderNotification(
            User user,
            NotificationType type,
            String title,
            String message,
            Long orderId
    ) {
        return Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceType(ORDER_REFERENCE_TYPE)
                .referenceId(orderId)
                .isRead(false)
                .build();
    }

    private String getOrderStatusLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Chờ xác nhận";
            case CONFIRMED -> "Đã xác nhận";
            case SHIPPING -> "Đang giao hàng";
            case DELIVERED -> "Đã giao hàng";
            case COMPLETED -> "Hoàn tất";
            case CANCELLED -> "Đã hủy";
        };
    }
}
