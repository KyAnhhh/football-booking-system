package vn.phankyanh.shopbongda.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "vnpay")
@Getter
@Setter
public class VNPayConfig {
    
    private String tmnCode;

    private String secretKey;

    private String payUrl;

    private String returnUrl;
}
