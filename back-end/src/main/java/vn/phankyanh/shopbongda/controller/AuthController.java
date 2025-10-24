package vn.phankyanh.shopbongda.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.phankyanh.shopbongda.dto.request.AuthRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.AuthResponse;
import vn.phankyanh.shopbongda.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    ApiResponse<AuthResponse> authenticated(@RequestBody AuthRequest request){
        boolean result = authService.authenticate(request);
        return ApiResponse.<AuthResponse>builder()
                .data(AuthResponse.builder()
                        .authenticated(result)
                        .build())
                .build();
    }
}
