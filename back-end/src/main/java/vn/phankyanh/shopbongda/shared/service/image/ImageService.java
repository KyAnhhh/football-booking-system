package vn.phankyanh.shopbongda.shared.service.image;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file){
        try {
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                 ObjectUtils.asMap("resource_type", "image"));

            // log raw result for debugging
            log.debug("Cloudinary upload result: {}", uploadResult);

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                // fallback to `url` key if `secure_url` not present
                secureUrl = uploadResult.get("url");
            }

            if (secureUrl == null) {
                log.error("Cloudinary upload returned no secure_url/url, full result: {}", uploadResult);
                throw new RuntimeException("Upload image failed: cloudinary response missing url");
            }

            return secureUrl.toString();  // trả về link ảnh
        } catch (Exception e) {
            log.error("Upload image failed", e);
            throw new RuntimeException("Upload image failed: " + e.getMessage());
        }
    }
}
