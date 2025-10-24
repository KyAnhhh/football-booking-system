package vn.phankyanh.shopbongda.service;

import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.dto.request.AuthRequest;
import vn.phankyanh.shopbongda.exception.AppException;
import vn.phankyanh.shopbongda.exception.ErrorCode;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public boolean authenticate(AuthRequest request){
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        return passwordEncoder.matches(request.getPassword(),  user.getPassword());
            }
}
