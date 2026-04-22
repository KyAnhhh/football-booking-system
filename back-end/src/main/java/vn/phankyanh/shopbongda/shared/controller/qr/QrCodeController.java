package vn.phankyanh.shopbongda.shared.controller.qr;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.service.BookingService;
import vn.phankyanh.shopbongda.order.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.order.service.OrderService;
import vn.phankyanh.shopbongda.shared.service.qr.QrCodeService;

@RestController
@RequiredArgsConstructor
public class QrCodeController {
    private final QrCodeService qrCodeService;
    private final OrderService orderService;
    private final BookingService bookingService;
    
    @GetMapping(value = "/api/public/order/{orderId}/generate-qr", 
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQrCode(@PathVariable("orderId") Long orderId) {
       OrderResponse order = orderService.getOrderForQr(orderId);
       String qrContent = 
            "NGAN_HANG: VCB DigiBank\n" +
            "STK: 1040763843\n" +
            "TEN: PHAN KY ANH\n" +
            "SO_TIEN: " + order.getTotalAmount() + "\n" +
            "NOI_DUNG: DonHang " + order.getId();
       return qrCodeService.generateQrCode(qrContent);
    }

    @GetMapping(value = "/api/public/booking/{bookingId}/generate-qr", 
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQrCodeForBooking(@PathVariable("bookingId") Long bookingId) {
       BookingResponse booking = bookingService.getBookingForQr(bookingId);
       String qrContent = 
            "NGAN_HANG: VCB DigiBank\n" +
            "STK: 1040763843\n" +
            "TEN: PHAN KY ANH\n" +
            "SO_TIEN: " + booking.getTotalPrice() + "\n" +
            "NOI_DUNG: DonHang " + booking.getId();
       return qrCodeService.generateQrCode(qrContent);
    }
}