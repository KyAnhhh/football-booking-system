package vn.phankyanh.shopbongda.notification.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.notification.dto.response.NotificationResponse;
import vn.phankyanh.shopbongda.notification.service.NotificationService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = jwt.getClaim("id");
        ApiResponse<List<NotificationResponse>> response = new ApiResponse<>();
        response.setData(notificationService.getMyNotifications(userId, limit));
        return response;
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> countUnread(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<Long> response = new ApiResponse<>();
        response.setData(notificationService.countUnread(userId));
        return response;
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = jwt.getClaim("id");
        notificationService.markAsRead(notificationId, userId);
        return new ApiResponse<>();
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        notificationService.markAllAsRead(userId);
        return new ApiResponse<>();
    }
}
