package vn.phankyanh.shopbongda.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.domain.RedisToken;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.repository.RedisTokenRepository;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final RedisTokenRepository redisTokenRepository;

    public String generateAccessToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date issueTime = new Date();
        Date expireTime = Date.from(issueTime.toInstant().plus(30, ChronoUnit.MINUTES));
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(issueTime)
                .expirationTime(expireTime)
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    public String generateRefreshToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date issueTime = new Date();
        Date expireTime = Date.from(issueTime.toInstant().plus(14, ChronoUnit.DAYS));
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(issueTime)
                .expirationTime(expireTime)
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    public boolean verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if(expirationTime.before(new Date())){
            return false;
        }
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Optional<RedisToken> byId = redisTokenRepository.findById(jwtId);
        if(byId.isPresent()){
            throw new RuntimeException("Token invalid");
        }
        return signedJWT.verify(new MACVerifier(secretKey));
    }

    public JwtInfo parseToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String JwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date issueTime = signedJWT.getJWTClaimsSet().getIssueTime();
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        return JwtInfo.builder()
                .jwtId(JwtId)
                .issueTime(issueTime)
                .expiredTime(expiredTime)
                .build();

    }
}
