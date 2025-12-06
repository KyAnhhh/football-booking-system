package vn.phankyanh.shopbongda.config;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.phankyanh.shopbongda.service.JwtService;

@Component
@RequiredArgsConstructor
@Slf4j // create logger to annotation
public class JwtDecoderConfiguration implements JwtDecoder {

    @Value("${jwt.secret-key}")
    private String secretKey;


    private final JwtService jwtService;
    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
    log.info("Decoding JWT Token");
        try {
            if(!jwtService.verifyToken(token)){
                throw new RuntimeException("Invalid token");
            }
            if(Objects.isNull(nimbusJwtDecoder)){
                SecretKey secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HS512");
                nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();
            }
            Jwt jwt = nimbusJwtDecoder.decode(token);

                // Lấy role từ claim
                String role = jwt.getClaimAsString("role");
                if (role == null) role = "CLIENT"; // default nếu không có role
                if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
                }

                // Gắn authorities cho Spring Security (normalize to ROLE_ prefix)
                return Jwt.withTokenValue(jwt.getTokenValue())
                    .headers(h -> h.putAll(jwt.getHeaders()))
                    .claims(c -> c.putAll(jwt.getClaims()))
                    .claim("authorities", List.of(new SimpleGrantedAuthority(role)))
                    .build();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

}
