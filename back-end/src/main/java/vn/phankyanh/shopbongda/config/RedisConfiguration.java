package vn.phankyanh.shopbongda.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfiguration {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration redisConfig =
                new RedisStandaloneConfiguration();

        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(password);

        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder()
                        .useSsl()
                        .build();

        return new LettuceConnectionFactory(
                redisConfig,
                clientConfig
        );
    }
}
