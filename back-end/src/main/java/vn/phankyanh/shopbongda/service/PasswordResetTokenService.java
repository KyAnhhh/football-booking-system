package vn.phankyanh.shopbongda.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.PasswordResetToken;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.ResetPasswordRequest;
import vn.phankyanh.shopbongda.repository.PasswordResetTokenRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createPasswordResetToken(String email){
        System.out.println("Creating password reset token for email: " + email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        passwordResetTokenRepository.deleteByUser(user);

        String token = java.util.UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryTime(java.time.LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(passwordResetToken);

        return token;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (passwordResetToken.getExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);
    }
}
