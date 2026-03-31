package vn.phankyanh.shopbongda.user.controller;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.phankyanh.shopbongda.shared.service.email.EmailService;
import vn.phankyanh.shopbongda.user.dto.request.ForgotPasswordRequest;
import vn.phankyanh.shopbongda.user.dto.request.LoginRequest;
import vn.phankyanh.shopbongda.user.dto.request.ResetPasswordRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.user.dto.response.LoginResponse;
import vn.phankyanh.shopbongda.user.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.user.service.AuthenticationService;
import vn.phankyanh.shopbongda.user.service.PasswordResetTokenService;
import vn.phankyanh.shopbongda.user.service.UserService;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final EmailService emailService;

    private final PasswordResetTokenService passwordResetService;

    @PostMapping("/login")
    LoginResponse login(@RequestBody LoginRequest loginRequest){
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/register")
    public UserCreateResponse createUser(@RequestBody UserCreateRequest userCreateRequest ){
        return userService.handleCreateUser(userCreateRequest);
    }

    @PostMapping("/logout")
    void logout(@RequestHeader("Authorization") String authHeader) throws ParseException {
        String accessToken = authHeader.replace("Bearer", "");
        authenticationService.logout(accessToken);
    }   

    @PostMapping("/refresh")
    LoginResponse refresh(@RequestBody String refreshToken) throws Exception {
        return authenticationService.refresh(refreshToken);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String token = passwordResetService.createPasswordResetToken(request.getEmail());

        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        log.info("Generated reset link: {}", resetLink);

        emailService.sendEmail(request.getEmail(), token);

        return ResponseEntity.ok("Reset link sent to email");
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }

}
