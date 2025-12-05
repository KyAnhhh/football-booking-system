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
import vn.phankyanh.shopbongda.repository.RedisTokenRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisTokenRepository redisTokenRepository;

    public LoginResponse login(LoginRequest request){
        UsernamePasswordAuthenticationToken  authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        User user =  (User) authenticate.getPrincipal();

        // Debug thông tin user trước khi lưu token
        log.info("User email: {}, role: {}", user.getEmail(), user.getRole());

        TokenPayload accessPayload= jwtService.generateAccessToken(user);
        TokenPayload refreshPayload = jwtService.generateRefreshToken(user);

        try {
            long expireSeconds = (refreshPayload.getExpiredTime().getTime() - System.currentTimeMillis()) / 1000;
            if (expireSeconds <= 0) {
                throw new IllegalStateException("Refresh token has already expired");
            }
            RedisToken redisToken = RedisToken.builder()
                    .jwtId(refreshPayload.getJwtId())
                    .expiredTime(expireSeconds)
                    .build();
            log.info("Saving RedisToken: jwtId={}, expiredTime={}", redisToken.getJwtId(), redisToken.getExpiredTime());
            redisTokenRepository.save(redisToken);
        } catch (Exception e) {
            log.error("Save RedisToken failed", e);
            throw e;
        }

        log.info("Login success: {}", user.getEmail());

        //trả ra token và role
        return  LoginResponse.builder()
                .accessToken(accessPayload.getToken())
                .refreshToken(refreshPayload.getToken())
                .role(user.getRole().name()) // ROLE_ADMIN hoặc ROLE_CLIENT
                .build();

    }
    public void logout(String token) throws ParseException {
        JwtInfo jwtInfo = jwtService.parseToken(token);
        String jwtId = jwtInfo.getJwtId();
        Date issueTime = jwtInfo.getIssueTime();
        Date expiredTime = jwtInfo.getExpiredTime();

        if(expiredTime.before(new Date())){
            return;
        }

        RedisToken redisToken = RedisToken.builder()
                .jwtId(jwtId)
                .expiredTime(expiredTime.getTime() - issueTime.getTime())
                .build();

        redisTokenRepository.save(redisToken);
        log.info("Logout success");
    }

}
