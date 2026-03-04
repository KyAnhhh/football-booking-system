package vn.phankyanh.shopbongda.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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
}
