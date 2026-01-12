package vn.phankyanh.shopbongda.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String error = "UNAUTHORIZED";

        Throwable cause = authException.getCause();
        if (cause instanceof JwtException &&
            cause.getMessage() != null &&
            cause.getMessage().toLowerCase().contains("expired")) {

            error = "ACCESS_TOKEN_EXPIRED";
        }

        new ObjectMapper().writeValue(
                response.getOutputStream(),
                Map.of("error", error)
        );
    }
}
