package vn.phankyanh.shopbongda.service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.RedisToken;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.dto.TokenPayload;
import vn.phankyanh.shopbongda.repository.RedisTokenRepository;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final RedisTokenRepository redisTokenRepository;

    public TokenPayload generateAccessToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date issueTime = new Date();
        Date expireTime = Date.from(issueTime.toInstant().plus(30, ChronoUnit.MINUTES));

        String jwtId = UUID.randomUUID().toString();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("id", user.getId())
                .claim("role", user.getRole().name())
                .issueTime(issueTime)
                .expirationTime(expireTime)
                .jwtID(jwtId)
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        String token = jwsObject.serialize();
        return TokenPayload.builder()
                .token(token)
                .jwtId(jwtId)
                .expiredTime(expireTime)
                .build();

    }

    public TokenPayload generateRefreshToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date issueTime = new Date();
        Date expireTime = Date.from(issueTime.toInstant().plus(14, ChronoUnit.DAYS));

        String jwtId = UUID.randomUUID().toString();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("id", user.getId())
                .issueTime(issueTime)
                .expirationTime(expireTime)
                .jwtID(jwtId)
                .build();


        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        String token = jwsObject.serialize();

        return TokenPayload.builder()
                .token(token)
                .jwtId(jwtId)
                .expiredTime(expireTime)
                .build();
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

    public static JwtInfo parseToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        return JwtInfo.builder()
                .jwtId(claims.getJWTID())
                .issueTime(claims.getIssueTime())
                .expiredTime(claims.getExpirationTime())
                .id(claims.getLongClaim("id"))
                .build();
    }
}
