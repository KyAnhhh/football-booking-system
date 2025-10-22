package vn.phankyanh.shopbongda.controller;

import java.text.ParseException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.LoginRequest;
import vn.phankyanh.shopbongda.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.dto.response.LoginResponse;
import vn.phankyanh.shopbongda.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.service.AuthenticationService;
import vn.phankyanh.shopbongda.service.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

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
        String token = authHeader.replace("Bearer", "");
        authenticationService.logout(token);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication){
        // Principal lúc này là một đối tượng Jwt
        org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();

        String email = jwt.getSubject(); // = "sub" trong token
        String role = jwt.getClaim("role"); // = "ROLE_ADMIN" hoặc "ROLE_CLIENT"

        return ResponseEntity.ok(Map.of(
                "email", email,
                "role", role
        ));
    }
}
