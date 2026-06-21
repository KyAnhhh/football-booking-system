package vn.phankyanh.shopbongda.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableSpringDataWebSupport(
    pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // Áp dụng cho tất cả các endpoint
				.allowedOrigins("http://localhost:3000", "http://192.168.1.9:3000") // Danh sách origins được phép
				.allowedMethods("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS") // Các phương thức HTTP được phép
				.allowedHeaders("*") // Cho phép tất cả headers
				.allowCredentials(true) // Cho phép gửi cookie hoặc thông tin xác thực
				.maxAge(3600); // Thời gian cache CORS preflight request (giây)
	}
}
