package vn.phankyanh.shopbongda.service;

import java.text.ParseException;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.phankyanh.shopbongda.domain.RedisToken;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.dto.TokenPayload;
import vn.phankyanh.shopbongda.dto.request.LoginRequest;
import vn.phankyanh.shopbongda.dto.response.LoginResponse;
import vn.phankyanh.shopbongda.exception.AuthException;
import vn.phankyanh.shopbongda.repository.RedisTokenRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisTokenRepository redisTokenRepository;
    private final UserRepository userRepository;
    
    public LoginResponse login(LoginRequest request){
        UsernamePasswordAuthenticationToken  authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        User user =  (User) authenticate.getPrincipal();

        // Debug thông tin user trước khi lưu token
        log.info("User email: {}, role: {}", user.getEmail(), user.getRole());

        TokenPayload accessPayload= jwtService.generateAccessToken(user);
        TokenPayload refreshPayload = jwtService.generateRefreshToken(user);

        log.info("Login success: {}", user.getEmail());

        //trả ra token và role
        return  LoginResponse.builder()
                .accessToken(accessPayload.getToken())
                .refreshToken(refreshPayload.getToken())
                .role(user.getRole().name()) // ROLE_ADMIN hoặc ROLE_CLIENT
                .build();

    }

    public void logout(String accessToken) throws ParseException {
        JwtInfo jwtInfo = jwtService.parseToken(accessToken);
        String jwtId = jwtInfo.getJwtId();
        // Date issueTime = jwtInfo.getIssueTime();
        Date expiredTime = jwtInfo.getExpiredTime();

        if(expiredTime.before(new Date())){
            return;
        }

        long expireSeconds = (expiredTime.getTime() - System.currentTimeMillis()) / 1000;
        
        if (expireSeconds <= 0) return;
        
        RedisToken redisToken = RedisToken.builder()
                .jwtId(jwtId)
                .expiredTime(expireSeconds)
                .build();

        redisTokenRepository.save(redisToken);
        log.info("Logout success for jwtId: {}", jwtId);
    }

    public LoginResponse refresh(String refreshToken) throws Exception {
        JwtInfo jwtInfo = jwtService.parseToken(refreshToken);

        if(jwtInfo == null || jwtInfo.getExpiredTime().before(new Date())) {
            throw new AuthException("Invalid refresh token");
        }

        if(redisTokenRepository.existsById(jwtInfo.getJwtId())) {
            throw new AuthException("Refresh token revoked");
        }

        User user = userRepository.findById(jwtInfo.getId())
            .orElseThrow(() -> new AuthException("User not found"));

        // revoke old refresh token
        long expireSeconds = (jwtInfo.getExpiredTime().getTime() - System.currentTimeMillis()) / 1000;

        if (expireSeconds > 0) {
            redisTokenRepository.save(
                RedisToken.builder()
                    .jwtId(jwtInfo.getJwtId())
                    .expiredTime(expireSeconds)
                    .build()
            );
        }
        
        TokenPayload newAccess = jwtService.generateAccessToken(user);
        TokenPayload newRefresh = jwtService.generateRefreshToken(user);
        
        log.info("Refresh token rotated for user: {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(newAccess.getToken())
                .refreshToken(newRefresh.getToken())
                .role(user.getRole().name())
                .build();
    }
}