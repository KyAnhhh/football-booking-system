package vn.phankyanh.shopbongda.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.security.jwt.JwtAuthEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private static final String[] public_list = {
            "/api/auth/**", "/api/public/**"
    };

    private static final String[] admin_list = {
            "/api/admin/**"
    };

    private static final String[] user_list = {
            "/api/user/**"
    };

    private final UserDetailsService userDetailsService;
    private final JwtDecoderConfiguration jwtDecoderConfiguration;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role == null) role = "CLIENT"; 
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            return List.of(new SimpleGrantedAuthority(role));
        });
        http
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(public_list).permitAll()
                        .requestMatchers("/api/admin/dashboard/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/api/admin/users/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/membership-tiers/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/imports/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/inventory/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/bookings/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/api/admin/calendar/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/api/admin/orders/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/admin/extra-services/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/api/admin/extra-services/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/payments/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/fields/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/maintenance/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/pricing-rules/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/holiday-pricing-rules/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/holidays/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/vouchers/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/products/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/product-images/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/product-variants/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/brands/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/categories/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/sizes/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/admin/reviews/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(admin_list).hasRole("ADMIN")
                        .requestMatchers(user_list).hasAnyRole("CLIENT", "ADMIN", "MANAGER", "STAFF")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoderConfiguration)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authenticationProvider);
    }

}
