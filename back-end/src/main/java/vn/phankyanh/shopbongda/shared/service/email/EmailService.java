package vn.phankyanh.shopbongda.shared.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String token){
        String link = "http://localhost:3000/reset-password?token=" + token;
        // String link = "http://localhost:8080/reset-password?token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("phankyanh789@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject("Reset Password");
        mailMessage.setText("Click the link to reset your password: " + link);
        
        mailSender.send(mailMessage);
    }

    @Async
    public void sendEmailWhenOrderStatusChange(String to, Long orderId, OrderStatus status) {

        String orderCode = String.format("ORD-%06d", orderId);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("phankyanh789@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject("Order Status Update");

        mailMessage.setText(
                "Dear customer,\n\n" +
                "Your order #" + orderCode + " status has been updated.\n\n" +
                "New status: " + status + "\n\n" +
                "Thank you for shopping with us."
        );

        mailSender.send(mailMessage);
    }

    @Async
    public void sendEmailWhenBookingStatusChange(String to, Long bookingId, BookingStatus status) {

        String bookingCode = String.format("BOOK-%06d", bookingId);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("phankyanh789@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject("Booking Status Update");

        mailMessage.setText(
                "Dear customer,\n\n" +
                "Your booking #" + bookingCode + " status has been updated.\n\n" +
                "New status: " + status + "\n\n" +
                "Thank you for shopping with us."
        );

        mailSender.send(mailMessage);
    }
}