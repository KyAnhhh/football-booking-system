package vn.phankyanh.shopbongda.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import vn.phankyanh.shopbongda.dto.request.LoginRequest;
import vn.phankyanh.shopbongda.dto.response.LoginResponse;
import vn.phankyanh.shopbongda.service.AuthenticationService;
import vn.phankyanh.shopbongda.service.UserService;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    LoginResponse login(@RequestBody LoginRequest loginRequest){
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/auth/logout")
    void logout(@RequestHeader("Authorization") String authHeader) throws ParseException {
        String token = authHeader.replace("Bearer", "");
        authenticationService.logout(token);
    }
}
