package vn.phankyanh.shopbongda.shared.controller.qr;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.service.BookingService;
import vn.phankyanh.shopbongda.order.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.order.service.OrderService;

@RestController
@RequiredArgsConstructor
public class QrCodeController {
    private final OrderService orderService;
    private final BookingService bookingService;

    @GetMapping("/api/user/orders/{orderId}/qr-url")
    public String getQrUrl(@PathVariable Long orderId, @AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("id");

        OrderResponse order = orderService.getOrderForQr(orderId);

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Không có quyền");
        }

        String amount = String.valueOf(order.getTotalAmount().longValue());

        String qrUrl = "https://api.vietqr.io/image/970436-1040763843-compact2.png"
                + "?amount=" + amount
                + "&addInfo=DonHang" + order.getId()
                + "&accountName=PHAN%20KY%20ANH";

        return qrUrl;
    }

    @GetMapping("/api/user/booking/{bookingId}/qr-url")
    public String getBookingQrUrl(@PathVariable Long bookingId, @AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("id");

        BookingResponse booking = bookingService.getBookingForQr(bookingId);

        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("Không có quyền");
        }

        String amount = String.valueOf(booking.getTotalPrice().longValue());

        String qrUrl = "https://api.vietqr.io/image/970436-1040763843-compact2.png"
                + "?amount=" + amount
                + "&addInfo=Booking" + booking.getId()
                + "&accountName=PHAN%20KY%20ANH";

        return qrUrl;
    }
}