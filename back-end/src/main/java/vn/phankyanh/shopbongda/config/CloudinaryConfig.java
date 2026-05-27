package vn.phankyanh.shopbongda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "do8twj1im",
                "api_key", "728685513619631",
                "api_secret", "3u_2GOOfPbEXXtMS6CejJa8k4r0"));
    }
}
