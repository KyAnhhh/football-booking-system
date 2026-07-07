package vn.phankyanh.shopbongda.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseCompatibilityConfiguration {

    @Bean
    ApplicationRunner paymentStatusColumnCompatibilityRunner(JdbcTemplate jdbcTemplate) {
        return args -> jdbcTemplate.execute(
                "ALTER TABLE payments MODIFY COLUMN payment_status VARCHAR(40) NOT NULL"
        );
    }
}
