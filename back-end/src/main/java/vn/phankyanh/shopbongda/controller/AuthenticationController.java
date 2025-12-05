package vn.phankyanh.shopbongda.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.dto.request.LoginRequest;
import vn.phankyanh.shopbongda.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.LoginResponse;
import vn.phankyanh.shopbongda.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.service.AuthenticationService;
import vn.phankyanh.shopbongda.service.JwtService;
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


        @GetMapping("/profile/me")
        public ApiResponse<UserCreateResponse> getProfile(@RequestHeader("Authorization") String authHeader) throws Exception {
            ApiResponse<UserCreateResponse> apiResponse = new ApiResponse<>();
            String token = authHeader.replace("Bearer ", "");
            JwtInfo info = JwtService.parseToken(token);
            apiResponse.setData(userService.handleGetUserById(info.getId()));
            return apiResponse;
        }

}
